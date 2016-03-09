package com.ibm.rtc.rtc.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ibm.rtc.rtc.R;
import com.ibm.rtc.rtc.account.Account;
import com.ibm.rtc.rtc.account.AccountManager;
import com.ibm.rtc.rtc.core.UrlManager;
import com.ibm.rtc.rtc.core.VolleyQueue;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mHostView;
    private EditText mPortView;
    private View mProgressView;
    private View mLoginFormView;
    private boolean isLogining =false;
    private RequestQueue mRequestQueue;
    private UrlManager mUrlManager;

    private TextView mAdvanced;
    private boolean isShowAdvanced = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mRequestQueue = VolleyQueue.getInstance(this).getRequestQueue();
        mUrlManager = UrlManager.getInstance(this);

        // Set up the login form.
        mHostView = (EditText) findViewById(R.id.host);
        mPortView = (EditText) findViewById(R.id.port);
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mAdvanced = (TextView)findViewById(R.id.login_advanced);
        mAdvanced.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isShowAdvanced) {
                    showAdvancedOptions();
                } else {
                    closeAdvancedOptions();
                }
            }
        });
    }


    private void closeAdvancedOptions() {
        if (isShowAdvanced) {
            mAdvanced.setText(R.string.login_advanced);
            mHostView.setVisibility(View.GONE);
            mPortView.setVisibility(View.GONE);
            isShowAdvanced = false;
        }
    }

    private void showAdvancedOptions() {
        if (!isShowAdvanced) {
            mAdvanced.setText(R.string.login_advanced_close);
            mHostView.setVisibility(View.VISIBLE);
            mPortView.setVisibility(View.VISIBLE);
            isShowAdvanced = true;
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        //already in login state
        if (isLogining) {
            return;
        }
        isLogining = true;

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);
        mHostView.setError(null);
        mPortView.setError(null);

        // Store values at the time of the login attempt.
        final String username = mUsernameView.getText().toString();
        final String password = mPasswordView.getText().toString();
        final String host = mHostView.getText().toString();
        final Integer port = Integer.parseInt(mPortView.getText().toString());

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        //Check for a valid uri address
        if (TextUtils.isEmpty(host)) {
            mHostView.setError(getString(R.string.error_field_required));
            focusView = mHostView;
            cancel = true;
        }

        //Check for a valid port
        if (port < 0 && port > 65535) {
            mPortView.setError(getString(R.string.port_range));
            focusView = mPortView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView == mHostView || focusView == mPortView)
                showAdvancedOptions();
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            mUrlManager.setHost(host);
            mUrlManager.setPort(port);
            String loginUrl = mUrlManager.getLoginUrl();

            final String AUTHENTICATE_ATTR = "authenticated";
            JSONObject body = new JSONObject();
            try {
                body.put("username", username);
                body.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, loginUrl,
                  body, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    isLogining = false;
                    showProgress(false);
                    try {
                        if (response.getBoolean(AUTHENTICATE_ATTR)) {
                            //login success
                            onLoginSuccess(username, password);
                        } else {
                            mPasswordView.setError(getString(R.string.error_incorrect_password));
                            mPasswordView.requestFocus();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.toString());
                    }
                    if (mLoginFormView != null) {
                        Snackbar.make(mLoginFormView, R.string.error_server_error,
                                Snackbar.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    isLogining = false;
                    showProgress(false);
                    if (mLoginFormView != null) {
                        Snackbar.make(mLoginFormView, R.string.error_login_network,
                                Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
            loginRequest.setTag(TAG);
            mRequestQueue.add(loginRequest);
        }
    }

    private void onLoginSuccess(String username, String password) {
        mRequestQueue.cancelAll(TAG);
        AccountManager accountManager = AccountManager.getInstance(this);
        Account account = new Account(username, password);
        accountManager.saveAccount(account);

        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHostView.setText(mUrlManager.getmHost());
        mPortView.setText(String.valueOf(mUrlManager.getmPort()));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRequestQueue.cancelAll(TAG);
    }
}


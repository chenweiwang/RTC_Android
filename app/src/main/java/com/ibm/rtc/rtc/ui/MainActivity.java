package com.ibm.rtc.rtc.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.ibm.rtc.rtc.R;
import com.ibm.rtc.rtc.account.Account;
import com.ibm.rtc.rtc.account.AccountManager;
import com.ibm.rtc.rtc.model.Project;
import com.ibm.rtc.rtc.ui.fragment.ProjectsListFragment;
import com.ibm.rtc.rtc.ui.fragment.WorkitemsListFragment;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.List;

/**
 * Created by v-wajie on 2015/12/8.
 */
public class MainActivity extends AppCompatActivity
        implements AccountHeader.OnAccountHeaderListener, ProjectsListFragment.ProjectSwitchListener {
    private static final String TAG = "MainActivity";
    private static final String CURRENT_PROJECT = "CurrentProject";
    private static final String CURRENT_USER = "CurrentUser";
    private static final String USERNAME = "Username";

    private Toolbar mToolbar;
    private Drawer mDrawer;
    private View mContentView;
    private WorkitemsListFragment mWorkitemsListFragment;
    private ProjectsListFragment mProjectsListFragment;
    private Fragment mLastUsedFragment;
    private Project mCurrentProject;
    private List<Account> mAccountList;
    private Account mSelectedAccount;

    /**
     * Static Factory method for MainActivity.
     */
    public static void startActivity(Activity context, String username) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(USERNAME, username);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //在创建的时候判断是否已经有登录的账号，如果没有就启动LoginActivity
        AccountManager accountManager = AccountManager.getInstance(this);
        mAccountList = accountManager.getAccounts();

        if (mAccountList == null || mAccountList.isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        //when Launched from the login Activity, we can get the current login account.
        if (this.getIntent().getExtras() != null) {
            String username = this.getIntent().getExtras().getString(USERNAME);
            Log.d(TAG, "OnCreate: The selected user is: " + username);

            for (Account account : mAccountList) {
                if (account.getUsername().equals(username)) {
                    mSelectedAccount = account;
                    break;
                }
            }
        } else {
            //Or read the last account from SharedPreference.
            String jsonText = PreferenceManager.getDefaultSharedPreferences(this)
                    .getString(CURRENT_USER, null);
            if (jsonText != null) {
                Gson gson = new Gson();
                mSelectedAccount = gson.fromJson(jsonText, Account.class);
            }
        }

        setContentView(R.layout.generic_toolbar);
        mContentView = findViewById(R.id.content);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mToolbar.setTitle("RTC");
            setSupportActionBar(mToolbar);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        if (mToolbar != null) {
            mToolbar.setTitle(title);
        } else {
            super.setTitle(title);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mDrawer == null) {
            createDrawer();
        }
        //get the last used project.
        if (mCurrentProject == null) {
            String jsonText = PreferenceManager.getDefaultSharedPreferences(this)
                    .getString(CURRENT_PROJECT, null);
            if (jsonText != null) {
                Gson gson = new Gson();
                mCurrentProject = gson.fromJson(jsonText, Project.class);
                //set the workitem list as the default view.
                mDrawer.setSelection(R.id.drawer_workitems);
            } else {
                //no used project, pop up the project list
                mDrawer.setSelection(R.id.drawer_projects);
            }
        }
    }

    private void createDrawer() {

        AccountHeader accountHeader = buildHeader();
        //create the Drawer
        DrawerBuilder drawerBuilder = new DrawerBuilder().withActivity(this)
                .withToolbar(getToolbar())
                .withAccountHeader(accountHeader);
        drawerBuilder.addDrawerItems(new PrimaryDrawerItem().withName(R.string.drawer_menu_workitems)
                        .withIcon(Octicons.Icon.oct_list_unordered)
                        .withIdentifier(R.id.drawer_workitems), new PrimaryDrawerItem().withName(R.string.drawer_menu_projects)
                        .withIcon(Octicons.Icon.oct_server)
                        .withIdentifier(R.id.drawer_projects), new DividerDrawerItem(), new SecondaryDrawerItem()
                        .withName(R.string.drawer_menu_settings)
                        .withIcon(Octicons.Icon.oct_settings)
                        .withIdentifier(R.id.drawer_settings));

        drawerBuilder.addDrawerItems(new SecondaryDrawerItem().withName(R.string.drawer_menu_about)
                        .withIcon(Octicons.Icon.oct_info)
                        .withIdentifier(R.id.drawer_about)
                        .withSelectable(false), new SecondaryDrawerItem().withName(R.string.drawer_menu_sign_out)
                        .withIcon(Octicons.Icon.oct_sign_out)
                        .withIdentifier(R.id.drawer_sign_out)
                        .withSelectable(false));

        drawerBuilder.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                if (drawerItem != null) {
                    int identifier = drawerItem.getIdentifier();
                    switch (identifier) {
                        case R.id.drawer_workitems:
                            onWorkitemsSelected();
                            break;
                        case R.id.drawer_projects:
                            onProjectsSelected();
                            break;
                        case R.id.drawer_settings:
                            onSettingsSelected();
                            break;
                        case R.id.drawer_about:
                            onAboutSelected();
                            break;
                        case R.id.drawer_sign_out:
                            onSignOutSelected();
                            break;
                    }
                }

                return false;
            }
        });

        mDrawer = drawerBuilder.build();
    }

    private AccountHeader buildHeader() {

        AccountHeaderBuilder headerBuilder = new AccountHeaderBuilder().withActivity(this)
                //TODO set the header Background color
                .withHeaderBackground(R.color.cardview_dark_background)
                .withOnAccountHeaderListener(this);

        for (Account account : mAccountList) {
            ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem().withName(account.getUsername())
                    .withIdentifier(account.hashCode())
                    //TODO 头像设置
                    .withIcon(R.mipmap.ic_launcher);

            if (mSelectedAccount == null || account == mSelectedAccount) {
                profileDrawerItem.withSetSelected(true);
                mSelectedAccount = account;
            } else {
                profileDrawerItem.withSetSelected(false);
            }
            headerBuilder.addProfiles(profileDrawerItem);
        }


        /*ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem().withName("Jack")
                .withEmail("Jackwangcs@outlook.com")
                .withIcon(R.mipmap.ic_launcher)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        MainActivity.this.startActivity(intent);
                        return true;
                    }
                });

        headerBuilder.addProfiles(profileDrawerItem);*/
        return headerBuilder.build();
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
        return false;
    }

    private boolean onWorkitemsSelected() {
        if (mCurrentProject == null) {
            Snackbar.make(this.mContentView,
                    getString(R.string.please_select_project), Snackbar.LENGTH_SHORT).show();
            mDrawer.setSelection(R.id.drawer_projects);
            return false;
        }

        if (mWorkitemsListFragment == null) {
            mWorkitemsListFragment = WorkitemsListFragment.newInstance(mSelectedAccount, mCurrentProject);
        }
        setFragment(mWorkitemsListFragment, false);
        setTitle(getString(R.string.workitem_list_title) + mCurrentProject.getTitle());
        return true;
    }

    private boolean onProjectsSelected() {
        //Snackbar.make(mContentView, "Projects clicked", Snackbar.LENGTH_SHORT).show();
        if (mProjectsListFragment == null) {
            mProjectsListFragment = ProjectsListFragment.newInstance(mSelectedAccount);
            mProjectsListFragment.setmProjectSwitchListener(this);
        }
        clearFragments();
        setFragment(mProjectsListFragment, false);
        setTitle(R.string.project_list_title);
        return true;
    }


    private boolean onAboutSelected() {
        return false;
    }

    private boolean onSignOutSelected() {
        AccountManager accountManager = AccountManager.getInstance(this);
        try {
            accountManager.removeAccount(mSelectedAccount.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.remove(CURRENT_PROJECT);
        editor.remove(CURRENT_USER);
        editor.apply();

        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivity(intent);
        finish();

        return true;
    }

    private void clearFragments() {
        mWorkitemsListFragment = null;
        if (getSupportFragmentManager() != null) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public boolean onSettingsSelected() {
        Snackbar.make(mContentView, "Settings clicked", Snackbar.LENGTH_SHORT).show();
        return true;
    }

    private void setFragment(Fragment fragment, boolean addToBackStack) {
        try {
            if (fragment != null && getSupportFragmentManager() != null) {
                this.mLastUsedFragment = fragment;
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (transaction != null) {
                    transaction.replace(R.id.content, fragment);
                    if (addToBackStack) {
                        transaction.addToBackStack(null);
                    }
                    transaction.commit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * handle the back button pressed event.
     * */
    @Override
    public void onBackPressed() {
        if (mDrawer != null && mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        } else {
            if (mLastUsedFragment instanceof WorkitemsListFragment) {
                finish();
            } else if (mDrawer != null) {
                mDrawer.setSelection(R.id.drawer_workitems);
                onWorkitemsSelected();
            }
        }
    }

    /**
     * listen to the project switch, when the current project has been switched,
     * save it, and set the drawer selection to workitems.
     * */
    @Override
    public void onProjectSwitch(Project project) {
        if (mCurrentProject == null || !mCurrentProject.getUuid().equals(project.getUuid())) {
            mCurrentProject = project;
            Gson gson = new Gson();
            String jsonText = gson.toJson(mCurrentProject);
            SharedPreferences.Editor editor =
                    PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putString(CURRENT_PROJECT, jsonText);
            editor.apply();
        }
        mDrawer.setSelection(R.id.drawer_workitems);
    }
}

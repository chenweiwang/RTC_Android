package com.ibm.rtc.rtc.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by v-wajie on 2015/12/15.
 */
public class UrlManager {

    private static final String TAG = "UrlManager";
    private static final String LOGIN_PATH = "/authenticate";
    private static final String LOCALHOST = "localhost";

    private static final String DEFAULT_PROTOCOL = "http://";
    private static final String HOST = "host";
    private static final String PORT = "port";

    private String mHost = null;
    private Integer mPort;
    private String mProtocol;
    private SharedPreferences mPrefs;
    private Context mCtx;

    private static UrlManager mInstance;

    public static UrlManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new UrlManager(context);
        }
        return mInstance;
    }

    private UrlManager(Context ctx) {
        this.mCtx = ctx.getApplicationContext();
        mProtocol = DEFAULT_PROTOCOL;
        getLoginUrl();
    }

    private SharedPreferences getPrefs() {
        if (mPrefs == null) {
            mPrefs = PreferenceManager.getDefaultSharedPreferences(mCtx);
        }
        return mPrefs;
    }

    public String getRootUrl() {
        if (mHost == null) {
            mHost = getPrefs().getString(HOST, "opentechtest.chinacloudapp.cn");
            //mHost = getPrefs().getString(HOST, "10.205.18.171");
            mPort = getPrefs().getInt(PORT, 4567);
            if (mHost != null) {
                return mProtocol + mHost + ":" + mPort + "/";
            } else {
                //TODO throws Exception instead return the localhost.
                return mProtocol + LOCALHOST + ":" + mPort + "/";
            }
        }
        return mProtocol + mHost + ":" + mPort + "/api";
    }

    public String getWorkitemUrl(int id) {
        return getRootUrl() + "workitems?id=" + id;
    }

    public String getLoginUrl() {
        return getRootUrl() + LOGIN_PATH;
    }

    public String getmHost() {
        synchronized (mHost) {
            return mHost;
        }
    }

    public Integer getmPort() {
        synchronized (mPort) {
            return mPort;
        }
    }

    public void setHost(String host) {
        synchronized (mHost) {
            if (mHost == null || !host.equals(mHost)) {
                mHost = host;
                SharedPreferences.Editor editor = getPrefs().edit();
                editor.putString(HOST, host);
                editor.apply();
            }
        }
    }

    public void setPort(int port) {
        synchronized (mPort) {
            if (mPort != port) {
                mPort = port;
                SharedPreferences.Editor editor = getPrefs().edit();
                editor.putInt(PORT, port);
                editor.apply();
            }

        }
    }

    public void setProtocol(String protocol) throws Exception {
        synchronized (mProtocol) {
            if (protocol.equals("http") || protocol.equals("https")) {
                mProtocol = protocol + "://";
            } else {
                throw new Exception("Unsupported protocol, only http and https available!");
            }
        }
    }
}

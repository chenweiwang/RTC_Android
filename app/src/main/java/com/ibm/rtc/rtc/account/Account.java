package com.ibm.rtc.rtc.account;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by v-wajie on 2015/12/11.
 */
public class Account implements Parcelable {
    private final String username;
    private final String host;
    private final int port;
    private String password;
    private String token;

    public Account(String username, String password, String host, int port, String token) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.token = token;
    }

    public Account(String username, String password, String host, int port) {
        this(username, password, host, port, null);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(host);
        dest.writeInt(port);
        dest.writeString(password);
        dest.writeString(token);
    }

    protected Account(Parcel in) {
        username = in.readString();
        host = in.readString();
        port = in.readInt();
        password = in.readString();
        token = in.readString();
    }


    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

}


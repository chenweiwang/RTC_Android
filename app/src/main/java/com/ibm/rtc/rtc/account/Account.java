package com.ibm.rtc.rtc.account;

/**
 * Created by v-wajie on 2015/12/11.
 */
public class Account {
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
}


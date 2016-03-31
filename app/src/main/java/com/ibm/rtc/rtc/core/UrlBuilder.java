package com.ibm.rtc.rtc.core;

import com.ibm.rtc.rtc.account.Account;

/**
 * Created by v-wajie on 3/9/2016.
 * UrlBuilder is used for building a url for the corresponding request.
 */
public class UrlBuilder {

    private static final String DEFAULT_PROTOCOL = "http://";
    private static final String LOGIN_PATH = "/authenticate";
    private static final String API_PATH = "/api";
    private static final String PROJECTS_PATH = "/projects";
    private static final String WORKITEMS_PATH = "/workitems";
    private static final String COMMENTS_PATH = "/comments";

    private String host;
    private int port;
    private String token;
    private String projectUuid =null;
    private int workitemId = -1;

    public UrlBuilder() {
        this.host = null;
        this.port = -1;
        this.token = null;
    }

    public UrlBuilder(Account account) {
        withAccount(account);
    }

    public UrlBuilder withAccount(Account account) {
        this.host = account.getHost();
        this.port = account.getPort();
        this.token = account.getToken();
        return this;
    }

    private boolean validHostAndPort() {
        return host != null && !host.isEmpty() && port > 0;
    }

    private boolean validToken() {
        return token != null && !token.isEmpty();
    }

    public String buildLoginUrl() {
        if (!validHostAndPort()) {
            throw new RuntimeException("Host or port must be provided.");
        }

        String url = DEFAULT_PROTOCOL + host + ":" + port + API_PATH + LOGIN_PATH;
        clear();
        return url;
    }

    public String buildProjectsUrl() {
        if (!validHostAndPort() || !validToken()) {
            throw new RuntimeException("Host, port or token must be provided.");
        }

        String url = DEFAULT_PROTOCOL + host + ":" + port + API_PATH + PROJECTS_PATH
                + "?token=" + token;
        clear();
        return url;
    }

    public UrlBuilder withProjectUUid(String uuid) {
        projectUuid =uuid;
        return this;
    }

    public UrlBuilder withWorkitemId(int workitemId) {
        this.workitemId = workitemId;
        return this;
    }

    public String buildWorkitemQueryUrl() {
        if (!validHostAndPort() || !validToken()) {
            throw new RuntimeException("Host, port or token must be provided.");
        }

        StringBuilder builder = new StringBuilder();
        builder.append(DEFAULT_PROTOCOL).append(host).append(":").append(port)
                .append(API_PATH).append(WORKITEMS_PATH);
        builder.append("?token=").append(token);

        if (projectUuid != null && !projectUuid.isEmpty()) {
            builder.append("&uuid=").append(projectUuid);
        }
        if (workitemId >= 0) {
            builder.append("&id=").append(workitemId);
        }

        clear();
        return builder.toString();
    }

    public String buildCommentsUrl() {
        if (!validHostAndPort() || !validToken()) {
            throw new RuntimeException("Host, port or token must be provided.");
        }

        if (workitemId < 0) {
            throw new RuntimeException("Workitem id must be provided to query comments.");
        }

        String url = DEFAULT_PROTOCOL + host + ":" + port + API_PATH + COMMENTS_PATH + "/" + workitemId
                + "?token=" + token;
        clear();
        return url;
    }

    private void clear() {
        this.host = null;
        this.port = -1;
        this.token = null;
        this.projectUuid =null;
        this.workitemId = -1;
    }
}

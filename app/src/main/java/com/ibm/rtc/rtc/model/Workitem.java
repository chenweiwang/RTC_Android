package com.ibm.rtc.rtc.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by Jack on 2015/12/17.
 */
public class Workitem {

    private String projectUuid;
    private int id;
    private String type;
    private String filedAgainst;
    private String ownedBy;
    private String createdBy;
    private Date createdTime;
    private Date lastModifiedTime;
    private String title;
    private String priority;
    private String severity;
    private String commentsUrl;
    private String subscribersUrl;
    private String plannedFor;
    private Date dueDate;

    public static Collection<Workitem> fromJSONArray(JSONArray jsonArray) {
        List<Workitem> workitemList = new ArrayList<>();
        try {
            //workitemList = JSONArray
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject object = jsonArray.getJSONObject(i);
                Workitem workitem = new Workitem();

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getProjectUuid() {
        return projectUuid;
    }

    public void setProjectUuid(String projectUuid) {
        this.projectUuid = projectUuid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFiledAgainst() {
        return filedAgainst;
    }

    public void setFiledAgainst(String filedAgainst) {
        this.filedAgainst = filedAgainst;
    }

    public String getOwnedBy() {
        return ownedBy;
    }

    public void setOwnedBy(String ownedBy) {
        this.ownedBy = ownedBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getSubscribersUrl() {
        return subscribersUrl;
    }

    public void setSubscribersUrl(String subscribersUrl) {
        this.subscribersUrl = subscribersUrl;
    }

    public String getPlannedFor() {
        return plannedFor;
    }

    public void setPlannedFor(String plannedFor) {
        this.plannedFor = plannedFor;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getCommentsUrl() {
        return commentsUrl;
    }

    public void setCommentsUrl(String commentsUrl) {
        this.commentsUrl = commentsUrl;
    }
}

package com.ibm.rtc.rtc.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Jack on 2015/12/17.
 */
public class Workitem /* implements Parcelable*/ {

    public static final String TAG = "Workitem";
    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
    private String projectUuid;
    private int id;
    private String description;
    private String type;
    private WorkitemType typeIndentifier;
    private String filedAgainst;
    private String ownedBy;
    private String createdBy;
    private Date createdTime;
    private Date lastModifiedTime;
    private Date dueDate;
    private String title;
    private String priority;
    private String severity;
    private String commentsUrl;
    private String subscribersUrl;
    private String plannedFor;
    private String foundIn;
    private long estimate;
    private long timeSpent;
    private String businessValue;
    private String risk;
    private String impact;
    private String storyPoint;

    public Workitem() {}

    public static List<Workitem> fromJSONArray(JSONArray jsonArray) throws JSONException, ParseException {
        List<Workitem> workitemList = new ArrayList<>();
        //workitemList => JSONArray
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject object = jsonArray.getJSONObject(i);
            workitemList.add(fromJSON(object));
        }
        return workitemList;
    }

    public static Workitem fromJSON(JSONObject object) throws JSONException, ParseException {
        Workitem workitem = new Workitem();
        workitem.setId(object.getInt("id"));
        workitem.setProjectUuid(object.getString("projectUuid"));
        workitem.setSubscribersUrl(object.getString("subscribersUrl"));
        workitem.setDescription(object.getString("description"));
        workitem.setTitle(object.getString("title"));
        workitem.setSeverity(object.getJSONObject("severity").getString("title"));
        workitem.setPriority(object.getJSONObject("priority").getString("title"));
        workitem.setCreatedBy(object.getJSONObject("createdBy").getString("name"));
        workitem.setOwnedBy(object.getJSONObject("ownedBy").getString("name"));
        workitem.setFiledAgainst(object.getJSONObject("filedAgainst").getString("title"));
        workitem.setType(object.getJSONObject("type").getString("title"));
        workitem.setTypeIndentifier(WorkitemType.getType(
                object.getJSONObject("type").getString("identifier")));
        workitem.setFoundIn(object.getJSONObject("foundIn").getString("title"));

        workitem.setBusinessValue(object.getJSONObject("businessValue").getString("title"));
        if (workitem.getBusinessValue().equals("null"))
            workitem.setBusinessValue(null);
        workitem.setRisk(object.getJSONObject("risk").getString("title"));
        if (workitem.getRisk().equals("null"))
            workitem.setRisk(null);
        workitem.setStoryPoint(object.getJSONObject("storyPoint").getString("title"));
        if (workitem.getStoryPoint().equals("null"))
            workitem.setStoryPoint(null);

        if (!object.getString("estimate").equals("null")) {
            workitem.setEstimate(Long.parseLong(object.getString("estimate")));
        } else {
            workitem.setEstimate(-1);
        }

        if (!object.getString("timeSpent").equals("null")) {
            workitem.setTimeSpent(Long.parseLong(object.getString("timeSpent")));
        } else {
            workitem.setTimeSpent(-1);
        }

        if (!object.getString("dueDate").equals("null")) {
            workitem.setDueDate(dateFormat.parse(object.getString("dueDate")));
        } else {
            workitem.setDueDate(null);
        }
        if (!object.getString("createdTime").equals("null")) {
            workitem.setCreatedTime(dateFormat.parse(object.getString("createdTime")));
        } else {
            workitem.setCreatedTime(null);
        }
        if (!object.getString("lastModifiedTime").equals("null")) {
            workitem.setLastModifiedTime(dateFormat.parse(object.getString("lastModifiedTime")));
        } else {
            workitem.setLastModifiedTime(null);
        }
        return workitem;
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

    public WorkitemType getTypeIndentifier() {
        return typeIndentifier;
    }

    public void setTypeIndentifier(WorkitemType typeIndentifier) {
        this.typeIndentifier = typeIndentifier;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFoundIn() {
        return foundIn;
    }

    public void setFoundIn(String foundIn) {
        this.foundIn = foundIn;
    }

    public long getEstimate() {
        return estimate;
    }

    public void setEstimate(long estimate) {
        this.estimate = estimate;
    }

    public long getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(long timeSpent) {
        this.timeSpent = timeSpent;
    }

    public String getBusinessValue() {
        return businessValue;
    }

    public void setBusinessValue(String businessValue) {
        this.businessValue = businessValue;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public String getStoryPoint() {
        return storyPoint;
    }

    public void setStoryPoint(String storyPoint) {
        this.storyPoint = storyPoint;
    }

    public String getImpact() {
        return impact;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }

}

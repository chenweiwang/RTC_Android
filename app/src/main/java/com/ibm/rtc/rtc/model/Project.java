package com.ibm.rtc.rtc.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-wajie on 1/6/2016.
 */
public class Project {
    private String title;
    private String uuid;
    private String description;

    public static List<Project> fromJSONArray(JSONArray jsonArray) throws JSONException {
        List<Project> projectList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject object = jsonArray.getJSONObject(i);
            projectList.add(fromJSON(object));
        }
        return projectList;
    }

    public static Project fromJSON(JSONObject object) throws JSONException {
        Project project = new Project();
        project.setTitle(object.getString("title"));
        project.setUuid(object.getString("uuid"));
        return project;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

package com.ibm.rtc.rtc.core;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.ibm.rtc.rtc.model.Project;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by v-wajie on 1/6/2016.
 */
public class ProjectsRequest extends JsonRequest<List<Project>> {


    public ProjectsRequest(String url, Response.Listener<List<Project>> listener,
                           Response.ErrorListener errorListener) {
        super(Method.GET, url, (String) null, listener, errorListener);
    }

    @Override
    protected Response<List<Project>> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonArrayString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, "utf-8"));
            JSONArray jsonArray = new JSONArray(jsonArrayString);
            List<Project> projects = Project.fromJSONArray(jsonArray);
            return Response.success(projects, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }
}

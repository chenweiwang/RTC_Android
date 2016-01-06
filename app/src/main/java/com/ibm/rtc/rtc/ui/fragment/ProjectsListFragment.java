package com.ibm.rtc.rtc.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ibm.rtc.rtc.R;
import com.ibm.rtc.rtc.adapter.ProjectAdapter;
import com.ibm.rtc.rtc.core.ProjectsRequest;
import com.ibm.rtc.rtc.core.UrlManager;
import com.ibm.rtc.rtc.core.VolleyQueue;
import com.ibm.rtc.rtc.model.Project;
import com.ibm.rtc.rtc.ui.base.LoadingListFragment;

import java.util.List;

/**
 * Created by v-wajie on 1/6/2016.
 */
public class ProjectsListFragment extends LoadingListFragment<ProjectAdapter> {
    private static final String TAG = "ProjectsListFragment";
    private RequestQueue mRequestQueue;
    private final int DEFAULT_STATUS_CODE = 500;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRequestQueue = VolleyQueue.getInstance(getActivity()).getRequestQueue();
    }

    public void setUpList(List<Project> projects) {
        ProjectAdapter adapter = new ProjectAdapter(LayoutInflater.from(getActivity()));
        adapter.setRecyclerAdapterContentListener(this);
        adapter.addAll(projects);

        setAdapter(adapter);
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();

        UrlManager urlManager = new UrlManager(getActivity());
        String projectUrl = urlManager.getRootUrl() + "projects";
        ProjectsRequest projectsRequest = new ProjectsRequest(projectUrl,
                new Response.Listener<List<Project>>() {
                    @Override
                    public void onResponse(List<Project> projects) {
                        if (projects != null && !projects.isEmpty()) {
                            hideEmpty();
                            if (refreshing || getAdapter() == null) {
                                setUpList(projects);
                            }
                        } else {
                            setEmpty();
                        }
                        stopRefresh();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d(TAG, "Fetch projects error: " + volleyError.getMessage());
                        stopRefresh();

                        setEmpty(true, volleyError.networkResponse == null ?
                                DEFAULT_STATUS_CODE : volleyError.networkResponse.statusCode);
                        if (getView() != null)
                            Snackbar.make(getView(), getText(R.string.workitem_refresh_error), Snackbar.LENGTH_SHORT).show();
                    }
                });
        projectsRequest.setTag(TAG);
        mRequestQueue.add(projectsRequest);
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_projects;
    }

    @Override
    public void onStop() {
        super.onStop();
        mRequestQueue.cancelAll(TAG);
    }
}

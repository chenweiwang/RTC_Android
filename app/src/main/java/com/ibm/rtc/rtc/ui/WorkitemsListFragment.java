package com.ibm.rtc.rtc.ui;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.ibm.rtc.rtc.adapter.WorkitemAdapter;
import com.ibm.rtc.rtc.core.UrlManager;
import com.ibm.rtc.rtc.core.VolleyQueue;
import com.ibm.rtc.rtc.model.Workitem;

import org.json.JSONArray;

import java.util.Collection;

/**
 * Created by Jack on 2015/12/23.
 */
public class WorkitemsListFragment extends LoadingListFragment<WorkitemAdapter> {
    private static final String TAG = "WorkitemsListFragment";
    private RequestQueue mRequestQueue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRequestQueue = VolleyQueue.getInstance(getActivity()).getRequestQueue();
    }

    public void setUpList(Collection<Workitem> workitems) {
        WorkitemAdapter adapter = new WorkitemAdapter(getActivity(),
                LayoutInflater.from(getActivity()));
        adapter.addAll(workitems);

        setAdapter(adapter);
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();

        UrlManager urlManager = new UrlManager(getActivity());
        String workitemsUrl = urlManager.getRootUrl() + "/workitems";
        JsonArrayRequest workitemsRequest = new JsonArrayRequest(workitemsUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {

                        if (true) {
                            hideEmpty();
                            if (refreshing || getAdapter() == null) {

                            }
                        }


                        stopRefresh();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        stopRefresh();
                    }
                });

    }

    @Override
    public void onStop() {
        super.onStop();
        mRequestQueue.cancelAll(TAG);
    }
}

package com.ibm.rtc.rtc.ui;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ibm.rtc.rtc.R;
import com.ibm.rtc.rtc.adapter.WorkitemAdapter;
import com.ibm.rtc.rtc.core.UrlManager;
import com.ibm.rtc.rtc.core.VolleyQueue;
import com.ibm.rtc.rtc.core.WorkitemsRequest;
import com.ibm.rtc.rtc.model.Workitem;
import com.ibm.rtc.rtc.ui.base.LoadingListFragment;

import java.util.List;

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addNewAdapter();
    }

    private void addNewAdapter() {
        WorkitemAdapter workitemAdapter = new WorkitemAdapter(getActivity(),
                LayoutInflater.from(getActivity()));
        workitemAdapter.setRecyclerAdapterContentListener(this);
        setAdapter(workitemAdapter);
    }

    public void setUpList(List<Workitem> workitems) {
        WorkitemAdapter adapter = new WorkitemAdapter(getActivity(),
                LayoutInflater.from(getActivity()));
        adapter.addAll(workitems);

        setAdapter(adapter);
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();

        UrlManager urlManager = new UrlManager(getActivity());
        String workitemsUrl = urlManager.getRootUrl() + "workitems";
        WorkitemsRequest workitemsRequest = new WorkitemsRequest(workitemsUrl,
            new Response.Listener<List<Workitem>>() {
                @Override
                public void onResponse(List<Workitem> workitems) {
                    if (workitems != null && !workitems.isEmpty()) {
                        hideEmpty();
                        if (refreshing || getAdapter() == null) {
                            setUpList(workitems);
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
                    Log.d(TAG, "Fetch workitems error: " + volleyError.getMessage());
                    stopRefresh();

                    if (volleyError.networkResponse != null)
                        setEmpty(true, volleyError.networkResponse.statusCode);
                    if (getView() != null)
                        Snackbar.make(getView(), getText(R.string.workitem_refresh_error), Snackbar.LENGTH_SHORT).show();
                }
            });
        workitemsRequest.setTag(TAG);
        mRequestQueue.add(workitemsRequest);
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_workitems;
    }

    @Override
    public void onStop() {
        super.onStop();
        mRequestQueue.cancelAll(TAG);
    }
}

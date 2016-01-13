package com.ibm.rtc.rtc.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ibm.rtc.rtc.R;
import com.ibm.rtc.rtc.core.UrlManager;
import com.ibm.rtc.rtc.core.WorkitemRequest;
import com.ibm.rtc.rtc.model.Workitem;
import com.ibm.rtc.rtc.ui.base.TitleProvider;
import com.ibm.rtc.rtc.ui.base.WorkitembaseFragment;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

/**
 * Created by v-wajie on 1/6/2016.
 */
public class WorkitemDetailFragment extends WorkitembaseFragment implements TitleProvider, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "WorkitemDetailFragment";

    private TextView description;
    private SwipeRefreshLayout swipe;
    private ViewGroup attributes;
    private Workitem workitem;
    private int count = 5;

    public static WorkitemDetailFragment newInstance(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt(WORKITEM_ID, id);
        WorkitemDetailFragment detailFragment = new WorkitemDetailFragment();
        detailFragment.setArguments(bundle);
        return detailFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_workitem_detail, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        if (swipe != null) {
            swipe.setOnRefreshListener(this);
            swipe.setColorSchemeColors(getActivity().getResources().getColor(R.color.primary_light));
        }

        description = (TextView) view.findViewById(R.id.description);
        attributes = (ViewGroup) view.findViewById(R.id.attributes);

        executeRequest();
    }

    private void executeRequest() {
        UrlManager urlManager = UrlManager.getInstance(getActivity());
        final String workitemUrl = urlManager.getWorkitemUrl(getWorkitemId());
        WorkitemRequest workitemRequest = new WorkitemRequest(workitemUrl, new Response.Listener<Workitem>() {
            @Override
            public void onResponse(Workitem item) {
                if (item != null) {
                    workitem = item;
                    setDisplayContent();
                } else {
                    if (getView() != null)
                        Snackbar.make(getView(), getString(R.string.workitem_refresh_error), Snackbar.LENGTH_SHORT);
                }
                stopRefresh();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (getView() != null)
                    Snackbar.make(getView(), getString(R.string.workitem_refresh_error), Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.button_retry, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    executeRequest();
                                }
                            }).show();
                if (workitem == null) {
                    displayError(volleyError.getCause().getMessage());
                }
                stopRefresh();
            }
        });
        workitemRequest.setTag(TAG);
        addToRequestQueue(workitemRequest);
    }

    private void displayError(String errorMsg) {
        description.setText(Html.fromHtml(
                "<h2> Error <h2>" +
                "<p>" + errorMsg + "</p>"
        ));
    }

    private void setUpAttributeList() {
        attributes.removeAllViews();


    }


    private void setDisplayContent() {
        description.setText(Html.fromHtml(getWorkitemDescription()));
        setUpAttributeList();
    }

    private String getWorkitemDescription() {
        String description = workitem.getDescription();
        if (description == null || description.isEmpty()) {
            description = getString(R.string.blank_description);
        }
        return "<h2>" + getString(R.string.workitem_description) + "</h2>"
                + description;
    }

    @Override
    public int getTitle() {
        return R.string.workitem_detail_title;
    }

    @Override
    public IIcon getTitleIcon() {
        return Octicons.Icon.oct_info;
    }

    @Override
    public void onRefresh() {
        executeRequest();
    }

    private void stopRefresh() {
        if (swipe != null) {
            swipe.post(new Runnable() {
                @Override
                public void run() {
                    swipe.setRefreshing(false);
                }
            });
        }
    }
}

package com.ibm.rtc.rtc.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ibm.rtc.rtc.R;
import com.ibm.rtc.rtc.adapter.AttributeItem;
import com.ibm.rtc.rtc.adapter.WorkitemAttributeAdapter;
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
    private RecyclerView recyclerView;
    private WorkitemAttributeAdapter adapter;

    public static WorkitemDetailFragment newInstance(Workitem workitem) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(WORKITEM_INFO, workitem);
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

        description = (TextView) view.findViewById(R.id.description);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);

        if (recyclerView != null) {
            final org.solovyev.android.views.llm.LinearLayoutManager linearLayoutManager =
                    new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            //linearLayoutManager.setChildSize(56);
            /*final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());*/
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }

        if (swipe != null) {
            swipe.setOnRefreshListener(this);
            swipe.setColorSchemeColors(getActivity().getResources().getColor(R.color.primary_light));
        }

        setDisplayContent();
    }

    private void executeRequest() {
        UrlManager urlManager = new UrlManager(getActivity());
        String workitemUrl = urlManager.getWorkitemUrl(getWorkitem().getId());
        WorkitemRequest workitemRequest = new WorkitemRequest(workitemUrl, new Response.Listener<Workitem>() {
            @Override
            public void onResponse(Workitem workitem) {
                if (workitem != null) {
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
                    Snackbar.make(getView(), getString(R.string.workitem_refresh_error), Snackbar.LENGTH_SHORT);
                stopRefresh();
            }
        });
        workitemRequest.setTag(TAG);
        addToRequestQueue(workitemRequest);
    }

    private void setUpAttributeList() {
        adapter = new WorkitemAttributeAdapter(LayoutInflater.from(getActivity()));
        adapter.add(new AttributeItem(Octicons.Icon.oct_alert, getWorkitem().getCreatedTime().toString(), null));
        adapter.add(new AttributeItem(Octicons.Icon.oct_alert, getWorkitem().getCreatedTime().toString(), null));
        adapter.add(new AttributeItem(Octicons.Icon.oct_alert, getWorkitem().getCreatedTime().toString(), null));
        adapter.add(new AttributeItem(Octicons.Icon.oct_alert, getWorkitem().getCreatedTime().toString(), null));
        adapter.add(new AttributeItem(Octicons.Icon.oct_alert, getWorkitem().getCreatedTime().toString(), null));
        adapter.add(new AttributeItem(Octicons.Icon.oct_alert, getWorkitem().getCreatedTime().toString(), null));

        recyclerView.setAdapter(adapter);
    }


    private void setDisplayContent() {
        description.setText(Html.fromHtml(getWorkitemDescription()));
        setUpAttributeList();
    }

    private String getWorkitemDescription() {
        String description = getWorkitem().getDescription();
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

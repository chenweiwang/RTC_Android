package com.ibm.rtc.rtc.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ibm.rtc.rtc.R;
import com.ibm.rtc.rtc.adapter.CommentAdapter;
import com.ibm.rtc.rtc.core.ProjectsRequest;
import com.ibm.rtc.rtc.core.UrlManager;
import com.ibm.rtc.rtc.core.VolleyQueue;
import com.ibm.rtc.rtc.model.Comment;
import com.ibm.rtc.rtc.model.Project;
import com.ibm.rtc.rtc.model.Workitem;
import com.ibm.rtc.rtc.ui.base.TitleProvider;
import com.ibm.rtc.rtc.ui.base.WorkitembaseFragment;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.ibm.rtc.rtc.ui.base.LoadingListFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by v-wajie on 1/6/2016.
 */
public class WorkitemCommentsFragment extends LoadingListFragment<CommentAdapter> implements TitleProvider {
    private static final String TAG = "CommentsFragment";
    private static final String WORKITEM_ID = "WORKITEM_ID";

    private RequestQueue mRequestQueue;
    private final int DEFAULT_STATUS_CODE = 500;

    private FloatingActionButton addComment;
    private EditText commentEditText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addComment = new FloatingActionButton(getContext());
        addComment.findViewById(R.id.addComment);
        addComment.show();
        mRequestQueue = VolleyQueue.getInstance(getActivity()).getRequestQueue();
    }

    List<Comment> comments = new ArrayList<>(); // 测试用的临时List
    Comment test1 = new Comment("test1", new Date(), "comment of test1");
    Comment test2 = new Comment("test2", new Date(), "comment of test2");
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        comments.add(test1);
        comments.add(test2);
        return inflater.inflate(R.layout.fragment_comments_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addComment = (FloatingActionButton)view.findViewById(R.id.addComment);
        addComment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.edit_comment_dialog, null);
                commentEditText = (EditText)view.findViewById(R.id.editComment);
                new MaterialDialog.Builder(getActivity())
                        .title("Add Your Comment")
                        .customView(view, false)
                        .positiveText(R.string.commit_comment)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                String comment = commentEditText.getText().toString();
                                // TODO: 2016/1/13  将结果写回服务器
                                Comment test3 = new Comment("new creator", new Date(), comment);
                                comments.add(test3);
                                setUpList(comments);
                            }
                        })
                        .negativeText(R.string.cancel_comment)
                        .show();
            }
        });
    }

    public void setUpList(List<Comment> comments) {
        CommentAdapter adapter = new CommentAdapter(getActivity(), LayoutInflater.from(getActivity()));
        adapter.setRecyclerAdapterContentListener(this);
        adapter.addAll(comments);
        setAdapter(adapter);
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();
        // TODO: 2016/1/12 add get comment
        UrlManager urlManager = UrlManager.getInstance(getActivity());
        String projectUrl = urlManager.getRootUrl() + "projects";
        ProjectsRequest projectsRequest = new ProjectsRequest(projectUrl,
                new Response.Listener<List<Project>>() {
                    @Override
                    public void onResponse(List<Project> projects) {
                        if (projects != null && !projects.isEmpty()) {
                            hideEmpty();
                            if (refreshing || getAdapter() == null) {
                                setUpList(comments);
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
        return R.string.no_comments;
    }

    @Override
    public void onStop() {
        super.onStop();
        mRequestQueue.cancelAll(TAG);
    }
    public static WorkitemCommentsFragment newInstance(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt(WORKITEM_ID, id);

        WorkitemCommentsFragment commentsFragment = new WorkitemCommentsFragment();
        commentsFragment.setArguments(bundle);
        return commentsFragment;
    }

    @Override
    public int getTitle() {
        return R.string.workitem_comment_title;
    }

    @Override
    public IIcon getTitleIcon() {
        return Octicons.Icon.oct_comment_discussion;
    }
}

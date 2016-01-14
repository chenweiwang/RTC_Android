package com.ibm.rtc.rtc.ui.fragment;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ibm.rtc.rtc.R;
import com.ibm.rtc.rtc.adapter.CommentAdapter;
import com.ibm.rtc.rtc.core.CommentsRequest;
import com.ibm.rtc.rtc.core.ProjectsRequest;
import com.ibm.rtc.rtc.core.UrlManager;
import com.ibm.rtc.rtc.core.VolleyQueue;
import com.ibm.rtc.rtc.core.WorkitemRequest;
import com.ibm.rtc.rtc.model.Comment;
import com.ibm.rtc.rtc.model.Project;
import com.ibm.rtc.rtc.model.Workitem;
import com.ibm.rtc.rtc.ui.base.TitleProvider;
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

    private FloatingActionButton addCommentBtn;
    private EditText commentEditText;

    private int workitemId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        workitemId = getArguments().getInt(WORKITEM_ID);
        addCommentBtn = new FloatingActionButton(getContext());
        addCommentBtn.findViewById(R.id.addComment);
        addCommentBtn.show();
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
        addCommentBtn = (FloatingActionButton)view.findViewById(R.id.addComment);
        addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.edit_comment_dialog, null);
                commentEditText = (EditText) view.findViewById(R.id.editComment);
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
                                comments.add(0, test3);
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
        String commentsUrl = urlManager.getRootUrl() + "comments/" + workitemId;
        CommentsRequest commentsRequest = new CommentsRequest(commentsUrl,
            new Response.Listener<List<Comment>>() {
                @Override
                public void onResponse(List<Comment> comments) {
                    if (comments != null && !comments.isEmpty()) {
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
                    Log.d(TAG, "Fetch comments error: " + volleyError.getMessage());
                    stopRefresh();

                    //TODO 设置合适的错误类型信息
                    setEmpty(true, volleyError.networkResponse == null ?
                            DEFAULT_STATUS_CODE : volleyError.networkResponse.statusCode);

                    if (getView() != null)
                        Snackbar.make(getView(), getText(R.string.comment_list_refresh_error), Snackbar.LENGTH_SHORT).show();
            }
        });
        commentsRequest.setTag(TAG);
        mRequestQueue.add(commentsRequest);
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

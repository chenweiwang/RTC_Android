package com.ibm.rtc.rtc.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ibm.rtc.rtc.R;
import com.ibm.rtc.rtc.model.Comment;
import com.ibm.rtc.rtc.ui.WorkitemActivity;

import java.text.SimpleDateFormat;

/**
 * Created by Mubai on 2016/1/11.
 */
public class CommentAdapter extends RecyclerArrayAdapter<Comment, CommentAdapter.ViewHolder> {
    private final Resources resources;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public CommentAdapter(Context context, LayoutInflater inflater) {
        super(inflater);
        resources = context.getResources();
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, Comment comment) {
        holder.textCreator.setText(comment.getCreator());
        // TODO: 2016/1/12 date format 
        holder.textCreateTime.setText(dateFormat.format(comment.getCreatedTime()));
        // TODO: 2016/1/12 add avatar
        holder.textDescription.setText(comment.getDescription());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.row_comment, parent, false));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textCreator;
        public TextView textCreateTime;
        public TextView textDescription;

        private ViewHolder(View itemView) {
            super(itemView);
            textCreator = (TextView) itemView.findViewById(R.id.creator);
            textCreateTime = (TextView) itemView.findViewById(R.id.createTime);
            textDescription = (TextView) itemView.findViewById(R.id.description);
        }
    }
}

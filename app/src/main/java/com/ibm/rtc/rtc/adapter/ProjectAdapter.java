package com.ibm.rtc.rtc.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ibm.rtc.rtc.R;
import com.ibm.rtc.rtc.model.Project;

/**
 * Created by v-wajie on 1/6/2016.
 */
public class ProjectAdapter extends RecyclerArrayAdapter<Project, ProjectAdapter.ViewHolder> {


    public ProjectAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    protected void onBindViewHolder(ProjectAdapter.ViewHolder holder, Project item) {
        holder.textTitle.setText(item.getTitle());
        holder.textUUID.setText(item.getUuid());
        //TODO 服务器端应该返回一个Projectd的描述
        holder.textDescription.setVisibility(View.INVISIBLE);
    }

    @Override
    public ProjectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.row_project, parent, false));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textTitle;
        public TextView textUUID;
        public TextView textDescription;


        private ViewHolder(View itemView) {
            super(itemView);
            textTitle = (TextView) itemView.findViewById(R.id.title);
            textUUID = (TextView) itemView.findViewById(R.id.uuid);
            textDescription = (TextView) itemView.findViewById(R.id.description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

    }
}

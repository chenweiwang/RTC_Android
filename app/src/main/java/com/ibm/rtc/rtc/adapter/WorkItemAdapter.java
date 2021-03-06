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
import com.ibm.rtc.rtc.model.Workitem;

/**
 * Created by Jack on 2015/12/17.
 */
public class WorkItemAdapter extends RecyclerArrayAdapter<Workitem, WorkItemAdapter.ViewHolder> {

    private boolean showOwnerName = true;
    private final Resources resources;
    private WorkitemSelectedListener listener;

    public WorkItemAdapter(Context context, LayoutInflater inflater) {
        super(inflater);
        resources = context.getResources();
    }

    public void setWorkitemSelectedListener(WorkitemSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, Workitem item) {
        holder.textTitle.setText(item.getId() + ": " + item.getTitle());

        if (showOwnerName) {
            holder.textOwner.setText("Owner: " + item.getOwnedBy());
        } else {
            holder.textOwner.setText("Owner: Unknown");
        }

        //TODO 为workitem添加其他字段。
        String des = item.getDescription();
        if (des.isEmpty()) {
            holder.textDescription.setText("Description: No description");
        } else {
            holder.textDescription.setText("Description: " + Html.fromHtml(des));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.row_workitem, parent, false));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textTitle;
        public TextView textOwner;
        public TextView textDescription;


        private ViewHolder(View itemView) {
            super(itemView);
            textTitle = (TextView) itemView.findViewById(R.id.title);
            textOwner = (TextView) itemView.findViewById(R.id.owner);
            textDescription = (TextView) itemView.findViewById(R.id.description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Workitem workitem = getItem(getAdapterPosition());
                    if (workitem != null && listener != null) {
                        listener.onWorkitemSelected(workitem);
                    }
                }
            });
        }
    }

    public interface WorkitemSelectedListener {
        public void onWorkitemSelected(Workitem workitem);
    }
}

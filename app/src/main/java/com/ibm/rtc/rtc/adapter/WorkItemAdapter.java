package com.ibm.rtc.rtc.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ibm.rtc.rtc.R;
import com.ibm.rtc.rtc.model.Workitem;

/**
 * Created by Jack on 2015/12/17.
 */
public class WorkitemAdapter extends RecyclerArrayAdapter<Workitem, WorkitemAdapter.ViewHolder> {

    private boolean showOwnerName = true;
    private final Resources resources;
    private WorkitemAdapterListener workitemAdapterListener;

    public WorkitemAdapter(Context context, LayoutInflater inflater) {
        super(inflater);
        resources = context.getResources();
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, Workitem item) {
        holder.textTitle.setText(item.getTitle());

        if (showOwnerName) {
            holder.textOwner.setText(item.getOwnedBy());
        } else {
            holder.textOwner.setText("");
        }

        //TODO 为workitem添加其他字段。
        holder.textDescription.setVisibility(View.INVISIBLE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.row_workitem, parent, false));
    }

    public interface WorkitemAdapterListener {
        void onItem(Workitem workitem);
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

                }
            });
        }
    }
}

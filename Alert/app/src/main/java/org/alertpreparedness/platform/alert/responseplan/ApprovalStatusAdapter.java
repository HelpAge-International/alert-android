package org.alertpreparedness.platform.alert.responseplan;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tj on 12/12/2017.
 */

public class ApprovalStatusAdapter extends RecyclerView.Adapter<ApprovalStatusAdapter.ViewHolder> {

    private Context context;
    private List<ApprovalStatusObj> list;

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;

        @BindView(R.id.icon)
        ImageView icon;

        @BindView(R.id.status)
        TextView status;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public ApprovalStatusAdapter(Context context, List<ApprovalStatusObj> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ApprovalStatusAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_approval_status, parent, false);
        return new ApprovalStatusAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ApprovalStatusAdapter.ViewHolder holder, int position) {

        ApprovalStatusObj model = list.get(position);

        holder.title.setText(model.permisionLevel);

        switch (model.status) {
            case 0://grey
                holder.status.setTextColor(context.getResources().getColor(R.color.alertGray));
                holder.status.setText(R.string.waiting_approval);
                break;
            case 1://green
                holder.status.setTextColor(context.getResources().getColor(R.color.alertGreen));
                holder.status.setText(R.string.approved);
                break;
            case 2://amber
                holder.status.setTextColor(context.getResources().getColor(R.color.alertAmber));
                holder.status.setText(R.string.in_progress);
                break;
            case 3:
                holder.status.setTextColor(context.getResources().getColor(R.color.alertRed));
                holder.status.setText(R.string.needs_reviewing);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
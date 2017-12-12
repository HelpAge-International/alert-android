package org.alertpreparedness.platform.alert.responseplan;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.adapter.HazardAdapter;

import java.text.DateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tj on 12/12/2017.
 */

public class ResponsePlansAdapter extends RecyclerView.Adapter<ResponsePlansAdapter.ViewHolder> {

    private Context context;
    private List<ResponsePlanObj> items;
    private ItemSelectedListner listner;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.vLine)
        View line;

        @BindView(R.id.ivStatusIcon)
        ImageView statusIcon;

        @BindView(R.id.tvHazardType)
        TextView hazardType;

        @BindView(R.id.tvPercentComplete)
        TextView percentComplete;

        @BindView(R.id.tvDescription)
        TextView description;

        @BindView(R.id.tvStatus)
        TextView status;

        @BindView(R.id.tvLastUpdated)
        TextView lastUpdated;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


    public ResponsePlansAdapter(Context context, List<ResponsePlanObj> items, ItemSelectedListner listner) {
        this.context = context;
        this.items = items;
        this.listner = listner;
    }

    @Override
    public ResponsePlansAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_response_plan, parent, false);
        return new ResponsePlansAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ResponsePlansAdapter.ViewHolder holder, int position) {

        ResponsePlanObj model = items.get(position);

        holder.description.setText(model.description);
        holder.percentComplete.setText(String.format(context.getString(R.string.complete), model.completePercentage));
        holder.hazardType.setText(model.hazardType);
        holder.lastUpdated.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(model.lastUpdated));
        switch (model.status) {
            case 0://grey
                holder.line.setBackgroundColor(context.getResources().getColor(R.color.alertGray));
                holder.status.setTextColor(context.getResources().getColor(R.color.alertGray));
                holder.status.setText(R.string.waiting_approval);
                break;
            case 1://green
                holder.line.setBackgroundColor(context.getResources().getColor(R.color.alertGreen));
                holder.status.setTextColor(context.getResources().getColor(R.color.alertGreen));
                holder.status.setText(R.string.approved);
                break;
            case 2://amber
                holder.line.setBackgroundColor(context.getResources().getColor(R.color.alertAmber));
                holder.status.setTextColor(context.getResources().getColor(R.color.alertAmber));
                holder.status.setText(R.string.in_progress);
                break;
            case 3:
                holder.line.setBackgroundColor(context.getResources().getColor(R.color.alertRed));
                holder.status.setTextColor(context.getResources().getColor(R.color.alertRed));
                holder.status.setText(R.string.needs_reviewing);
                break;
        }
        holder.itemView.setOnClickListener((v) -> listner.onResponsePlanSelected(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface ItemSelectedListner {
        void onResponsePlanSelected(int pos);
    }
}

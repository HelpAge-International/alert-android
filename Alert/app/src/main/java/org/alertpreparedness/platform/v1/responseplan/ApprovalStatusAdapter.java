package org.alertpreparedness.platform.v1.responseplan;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.alertpreparedness.platform.v1.R;

import java.util.ArrayList;
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

        @BindView(R.id.rvNotes)
        RecyclerView notesView;

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
            case 1://grey
                holder.status.setTextColor(context.getResources().getColor(R.color.alertGray));
                holder.status.setText(R.string.waiting_approval);
                holder.icon.setImageResource(R.drawable.icon_pending_grey);
                break;
            case 2://green
                holder.status.setTextColor(context.getResources().getColor(R.color.alertGreen));
                holder.status.setText(R.string.approved);
                holder.icon.setImageResource(R.drawable.icon_status_complete);
                break;
            case 0://amber
                holder.status.setTextColor(context.getResources().getColor(R.color.alertAmber));
                holder.status.setText(R.string.requires_submission);
                holder.icon.setImageResource(R.drawable.icon_pending_amber);
                break;
            case 3:
                holder.status.setTextColor(context.getResources().getColor(R.color.alertRed));
                holder.status.setText(R.string.needs_reviewing);
                holder.icon.setImageResource(R.drawable.icon_status_needs_reviewing);
                holder.notesView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                holder.notesView.setAdapter(new ApprovalNotesAdapter(new ArrayList<>(model.notes.values())));
                break;
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
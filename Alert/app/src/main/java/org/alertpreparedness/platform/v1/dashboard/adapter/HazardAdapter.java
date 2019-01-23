package org.alertpreparedness.platform.v1.dashboard.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.alertpreparedness.platform.v1.ExtensionHelperKt;
import org.alertpreparedness.platform.v1.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tj on 12/12/2017.
 */

public class HazardAdapter extends RecyclerView.Adapter<HazardAdapter.ViewHolder> {

    private List<String> items = new ArrayList<>();
    private HazardSelectionListner listner;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.ivIcon)
        ImageView icon;

        @BindView(R.id.tvField)
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public HazardAdapter(HazardSelectionListner listner) {
        this.listner = listner;
        items = ExtensionHelperKt.getHazardTypes();
    }

    @Override
    public HazardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hazard, parent, false);
        return new HazardAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HazardAdapter.ViewHolder holder, int position) {
        holder.icon.setImageResource(ExtensionHelperKt.getHazardImg(items.get(position)));
        holder.textView.setText(items.get(position));
        holder.itemView.setOnClickListener((v) -> listner.onHazardSelected(position, items.get(position)));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface HazardSelectionListner {
        void onHazardSelected(int type, String hazardTitle);
    }
}

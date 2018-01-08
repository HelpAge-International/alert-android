package org.alertpreparedness.platform.alert.adv_preparedness.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.alertpreparedness.platform.alert.R;

/**
 * Created by faizmohideen on 06/01/2018.
 */

public class NoActionAdapter extends RecyclerView.Adapter<NoActionAdapter.ViewHolder> {
    @Override
    public NoActionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_no_action, parent, false);
        return new NoActionAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NoActionAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}

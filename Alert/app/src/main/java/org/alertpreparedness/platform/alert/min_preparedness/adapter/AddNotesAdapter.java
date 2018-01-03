package org.alertpreparedness.platform.alert.min_preparedness.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.alertpreparedness.platform.alert.min_preparedness.model.Notes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by faizmohideen on 03/01/2018.
 */

public class AddNotesAdapter extends RecyclerView.Adapter<AddNotesAdapter.ViewHolder> {

    private List<Notes> notes;
    @Override
    public AddNotesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(AddNotesAdapter.ViewHolder holder, int position) {

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

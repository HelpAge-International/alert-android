package org.alertpreparedness.platform.alert.utils;

/**
 * Created by Tj on 05/12/2017.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.MyViewHolder> {

    private List<String> strings;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textview)
        public TextView view;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public SimpleAdapter(List<String> moviesList) {
        this.strings = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_text, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.view.setText(strings.get(position));
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }
}
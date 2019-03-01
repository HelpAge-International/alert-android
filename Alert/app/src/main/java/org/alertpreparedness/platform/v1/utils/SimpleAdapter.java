package org.alertpreparedness.platform.v1.utils;

/**
 * Created by Tj on 05/12/2017.
 */

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.alertpreparedness.platform.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.MyViewHolder> {

    private int positionInParent;
    private List<String> strings;
    private RemoveListener listener;

    public int getPositionInParent() {
        return positionInParent;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textview)
        public TextView view;

        @BindView(R.id.remove)
        TextView remove;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public SimpleAdapter(int positionInParent, List<String> lit, RemoveListener listener) {
        this.positionInParent = positionInParent;
        this.strings = lit;
        this.listener = listener;
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
        holder.remove.setOnClickListener(view -> listener.onItemRemove(positionInParent,position));
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    public interface RemoveListener {
        void onItemRemove(int positionInParent, int position);
    }

    public void addItem(int position){
        notifyItemChanged(position);
    }


    //add item
    //notifyItemInserted
}
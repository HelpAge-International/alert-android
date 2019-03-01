package org.alertpreparedness.platform.v1.dashboard.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.alertpreparedness.platform.R;
import org.alertpreparedness.platform.v1.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tj on 12/12/2017.
 */

public class MultiHazardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Constants.Hazard[] items;
    private MultiHazardSelectionListener listener;

    private List<Constants.Hazard> selected = new ArrayList<>();

    public List<Constants.Hazard> getSelected() {
        return selected;
    }

    public void selectAll(boolean selectAll) {
        selected.clear();
        if(selectAll) {
            selected.addAll(Arrays.asList(items));
        }
        notifyDataSetChanged();
        notifyListener();
    }

    public void setSelected(List<Constants.Hazard> toAdd){
        selected.clear();
        selected.addAll(toAdd);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.ivIcon)
        ImageView icon;

        @BindView(R.id.tvField)
        TextView textView;

        @BindView(R.id.checkBox)
        CheckBox checkBox;

        View itemView;

        private Constants.Hazard hazard;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            this.itemView = itemView;
        }

        public void bind(Constants.Hazard hazard){
            this.hazard = hazard;
            icon.setImageResource(hazard.getIconRes());
            textView.setText(hazard.getStringRes());
            checkBox.setChecked(selected.contains(hazard));
        }

        @Override
        public void onClick(View v) {
            checkBox.setChecked(toggle(hazard));
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.checkBox)
        CheckBox checkBox;

        View itemView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            this.itemView = itemView;
        }

        @Override
        public void onClick(View v) {
            checkBox.setChecked(toggleSelectAll());
        }

        public void bind(){
            checkBox.setChecked(selected.size() == items.length);
        }

    }


    private boolean toggleSelectAll() {
        if(selected.size() == items.length){
            selected.clear();
            notifyDataSetChanged();
            return false;
        }
        else{
            selected.clear();
            selected.addAll(Arrays.asList(items));
            notifyDataSetChanged();
            return true;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? R.layout.item_multi_hazard_header : R.layout.item_multi_hazard;
    }

    private boolean toggle(Constants.Hazard hazard) {
        boolean toReturn;
        if (selected.contains(hazard)) {
            selected.remove(hazard);
            toReturn = false;
        } else {
            selected.add(hazard);
            toReturn = true;
        }

        notifyItemChanged(0);
        notifyListener();

        return toReturn;
    }

    private void notifyListener() {
        listener.onHazardSelected(selected);
    }

    public MultiHazardAdapter(MultiHazardSelectionListener listener) {
        this.listener = listener;
        items = Constants.Hazard.values();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == R.layout.item_multi_hazard) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_multi_hazard, parent, false);
            return new MultiHazardAdapter.ViewHolder(itemView);
        }
        else{
            return new HeaderViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_multi_hazard_header, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == R.layout.item_multi_hazard){
            ((ViewHolder)holder).bind(items[position - 1]);
        }
        else{
            ((HeaderViewHolder)holder).bind();
        }
    }

    @Override
    public int getItemCount() {
        return items.length + 1;
    }

    public interface MultiHazardSelectionListener {
        void onHazardSelected(List<Constants.Hazard> selectedHazards);
    }
}

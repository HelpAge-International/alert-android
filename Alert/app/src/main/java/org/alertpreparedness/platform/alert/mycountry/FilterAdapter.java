package org.alertpreparedness.platform.alert.mycountry;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.utils.SimpleAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tj on 02/01/2018.
 */

class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.MyViewHolder> {

    private Context context;
    private List<String> strings;
    private FilterAdapter.SelectListener listener;

    private boolean enabled;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView view;

        @BindView(R.id.checkbox)
        AppCompatCheckBox checkBox;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public FilterAdapter(Context context, List<String> lit, FilterAdapter.SelectListener listener) {
        this.context = context;
        this.strings = lit;
        this.listener = listener;
    }

    @Override
    public FilterAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter, parent, false);

        return new FilterAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FilterAdapter.MyViewHolder holder, int position) {
        holder.view.setText(strings.get(position));
        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> listener.onItemSelected(position));
        if(enabled) {
            holder.view.setEnabled(true);
            holder.checkBox.setEnabled(true);
        }
        else {
//            holder.view.setTextColor(context.getResources().getColor(R.color.divider_gray));
            holder.view.setAlpha(0.5f);
            holder.checkBox.setAlpha(0.5f);
            holder.view.setEnabled(false);
            holder.checkBox.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    public void disableAll() {
        if(enabled) {
            enabled = false;
            notifyItemRangeChanged(0, getItemCount());
        }
    }

    public void enableAll() {
        if(!enabled) {
            enabled = true;
            notifyItemRangeChanged(0, getItemCount());
        }
    }

    public interface SelectListener {
        void onItemSelected(int position);
    }
}
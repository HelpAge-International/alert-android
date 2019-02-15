package org.alertpreparedness.platform.v1.mycountry;

import android.content.Context;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.alertpreparedness.platform.v1.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tj on 02/01/2018.
 */

class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.MyViewHolder> {

    private Context context;
    private HashMap<String, NetworkHolder> items = new HashMap<>();
    private List<String> keys = new ArrayList<>();
    private List<Integer> checkedItems = new ArrayList<>();
    private FilterAdapter.SelectListener listener;

    private boolean enabled;
    private boolean checked;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvTitle)
        TextView view;

        @BindView(R.id.checkbox)
        AppCompatCheckBox checkBox;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public HashMap<String, NetworkHolder> getItems() {
        return items;
    }

    public FilterAdapter(Context context, FilterAdapter.SelectListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public FilterAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter, parent, false);

        return new FilterAdapter.MyViewHolder(itemView);
    }

    public void addItem(String title, String networkId, String agencyId) {
        if(keys.indexOf(networkId) == -1) {
            keys.add(networkId);
            items.put(networkId, new NetworkHolder(title, agencyId));
            checkedItems.add(keys.size() - 1);
            notifyItemInserted(keys.size() - 1);
        }
        else {
            items.get(networkId).agencyIds.add(agencyId);
        }
    }

    public NetworkHolder getModel(int pos) {
        return items.get(keys.get(pos));
    }

    public List<Integer> getCheckedItems() {
        return checkedItems;
    }

    @Override
    public void onBindViewHolder(FilterAdapter.MyViewHolder holder, int position) {
        holder.view.setText(items.get(keys.get(position)).getTitle());
        holder.checkBox.setOnCheckedChangeListener((compoundButton, checked) -> {
            if(checked) {
                checkedItems.add(position);
            }
            else {
                checkedItems.remove(checkedItems.indexOf(position));
            }
            listener.onItemSelected(position);
        });
        if(checked) {
            holder.checkBox.setEnabled(true);
        }
        if(enabled) {
            holder.view.setEnabled(true);
        }
        else {
            holder.view.setAlpha(0.5f);
            holder.checkBox.setAlpha(0.5f);
            holder.view.setEnabled(false);
            holder.checkBox.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return keys.size();
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

    public void checkAll() {
        if(checked) {
            checked = true;
            notifyItemRangeChanged(0, getItemCount());
        }
    }

    public interface SelectListener {
        void onItemSelected(int position);
    }

    public final class NetworkHolder {

        private List<String> agencyIds = new ArrayList<>();
        private String title;

        public NetworkHolder(String title) {
            this.title = title;
        }

        public NetworkHolder(String title, String agencyId) {
            this.title = title;
            this.agencyIds.add(agencyId);
        }

        public void addId(String id) {
            agencyIds.add(id);
        }

        public List<String> getIds() {
            return agencyIds;
        }

        public String getTitle() {
            return title;
        }
    }
}
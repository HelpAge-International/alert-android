package org.alertpreparedness.platform.v1.adv_preparedness.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.alertpreparedness.platform.R;
import org.alertpreparedness.platform.v1.adv_preparedness.model.UserModel;

/**
 * Created by faizmohideen on 09/01/2018.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private final ArrayList<String> keys;
    private Context context;
    private List<UserModel> list;
    private HashMap<String, UserModel> items;
    private DatabaseReference dbRef;
    private UserListAdapter.ItemSelectedListener listener;
    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rbUsers)
        RadioButton radioButtonUsers;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.rbUsers)
        void onCLick(View v) {
            selectedUser = getItem(getAdapterPosition());
            listener.onActionItemSelected(getItem(getAdapterPosition()));
            notifyDataSetChanged();
        }
    }

    public UserListAdapter(Context context, DatabaseReference dbRef, UserListAdapter.ItemSelectedListener listener) {
        this.context = context;
        this.items = new HashMap<>();
        this.listener = listener;
        this.dbRef = dbRef;
        this.keys = new ArrayList<>(items.keySet());
    }

    public UserListAdapter(Context context, List<UserModel> list) {
        this.context = context;
        this.list = list;
        this.keys = new ArrayList<>(items.keySet());
    }

    private UserModel selectedUser;

    public void addUsers(String key, UserModel model) {
        if (keys.indexOf(key) == -1) {
                keys.add(key);
                items.put(key, model);
                notifyItemInserted(keys.size() - 1);
        }
    }

    @Override
    public UserListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_users_list, parent, false);
        return new UserListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserListAdapter.ViewHolder holder, int position) {
        UserModel user = items.get(keys.get(position));
        holder.radioButtonUsers.setText(user.getFullName());
        holder.radioButtonUsers.setChecked(user == selectedUser);
    }
    public UserModel getItem(int index) {
        return items.get(keys.get(index));
    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface ItemSelectedListener {
        void onActionItemSelected(UserModel user);
    }

}
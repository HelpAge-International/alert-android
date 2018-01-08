package org.alertpreparedness.platform.alert.adv_preparedness.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.adv_preparedness.model.AdvancedAction;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.helper.DateHelper;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faizmohideen on 05/01/2018.
 */

public class APActionAdapter extends RecyclerView.Adapter<APActionAdapter.ViewHolder> implements ChildEventListener {

    private final ArrayList<String> keys;
    private Context context;
    private HashMap<String, Action> items;
    private DatabaseReference dbRef;
    private APActionAdapter.ItemSelectedListener listener;
    private String dateFormat = "MMM dd,yyyy";
    private SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());

    @Inject
    @AgencyRef
    DatabaseReference dbAgencyRef;

    //In progress
    public void addInProgressItem(String key, Action action) {
        if (keys.indexOf(key) == -1) {
            if (action.getLevel() == Constants.APA
                    && action.getDueDate() != null
                    && !DateHelper.itWasDue(action.getDueDate())
                    && action.getTaskName() != null) {
                keys.add(key);
                items.put(key, action);
                notifyItemInserted(keys.size() - 1);
            }
        } else {
            items.put(key, action);
            notifyItemChanged(keys.indexOf(key));
        }

    }

    public void addExpiredItem(String key, Action action) {
        if (keys.indexOf(key) == -1) {
            if (action.getLevel() == Constants.APA
                    && action.getDueDate() != null
                    && DateHelper.itWasDue(action.getDueDate())
                    && action.getTaskName() != null) {
                keys.add(key);
                items.put(key, action);
                notifyItemInserted(keys.size() - 1);
            }
        } else {
            items.put(key, action);
            notifyItemChanged(keys.indexOf(key));
        }
    }

    public void addUnassignedItem(String key, Action action) {
        if (keys.indexOf(key) == -1) {
            if (action.getLevel() == Constants.APA
                    && action.getDueDate() != null
                    && !DateHelper.itWasDue(action.getDueDate())
                    && action.getTaskName() != null) {
                keys.add(key);
                items.put(key, action);
                notifyItemInserted(keys.size() - 1);
            }
        } else {
            items.put(key, action);
            notifyItemChanged(keys.indexOf(key));
        }
    }

    public void addCompletedItem(String key, Action action) {
        if (keys.indexOf(key) == -1) {
            if (action.getLevel() == Constants.APA
                    && action.getComplete() != null
                    && action.getComplete()
                    && action.getDueDate() != null) {
                keys.add(key);
                items.put(key, action);
                notifyItemInserted(keys.size() - 1);
            }
        } else {
            items.put(key, action);
            notifyItemChanged(keys.indexOf(key));
        }
    }

    public void addArchivedItem(String key, Action action) {
        if (keys.indexOf(key) == -1) {
            //  System.out.println("action = " + action.getArchived());
            if (action.getLevel() == Constants.APA
                    && action.getArchived() != null
                    && action.getArchived()
                    && action.getDueDate() != null) {
                keys.add(key);
                items.put(key, action);
                notifyItemInserted(keys.size() - 1);
            }
        } else {
            items.put(key, action);
            notifyItemChanged(keys.indexOf(key));
        }
    }

    public APActionAdapter(Context context, DatabaseReference dbRef, APActionAdapter.ItemSelectedListener listener) {
        this.context = context;
        this.items = new HashMap<>();
        this.listener = listener;
        this.dbRef = dbRef;
        this.listener = listener;
        this.keys = new ArrayList<>(items.keySet());
        this.dbRef.addChildEventListener(this);
    }

    public Action getItem(int index) {
        return items.get(keys.get(index));
    }

    @Override
    public APActionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_minimum_actions, parent, false);
        return new APActionAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(APActionAdapter.ViewHolder holder, int position) {

        System.out.println("position = " + position);
        Action action = items.get(keys.get(position));
        holder.tvActionType.setText(getActionType((int) action.getActionType()));
        holder.tvActionName.setText(action.getTaskName());
        holder.tvBudget.setText(getBudget(action.getBudget()));
        holder.tvDueDate.setText(getDate(action.getDueDate()));
        getDepartment(action.db, holder);
        holder.itemView.setOnClickListener((v) -> listener.onActionItemSelected(position, keys.get(position)));
    }

    private void getDepartment(DatabaseReference db, APActionAdapter.ViewHolder holder) {

        db.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String department = (String) dataSnapshot.child("departments").getChildren().iterator().next().child("name").getValue();
                holder.tvUserName.setText(department);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_action_type)
        TextView tvActionType;

        @BindView(R.id.tv_due_date)
        TextView tvDueDate;

        @BindView(R.id.tv_action_name)
        TextView tvActionName;

        @BindView(R.id.tv_user_name)
        TextView tvUserName;

        @BindView(R.id.tv_budget)
        TextView tvBudget;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private String getBudget(Long budget) {
        return "$" + budget;
    }

    private String getDate(Long date) {
        return "Due: " + format.format(new Date(date));
    }

    private String getActionType(int type) {
        switch (type) {
            case 0:
                return "CHS";
            case 1:
                return "Mandated";
            case 2:
                return "Custom";
            default:
                return "";
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public interface ItemSelectedListener {
        void onActionItemSelected(int pos, String key);
    }
}

package org.alertpreparedness.platform.alert.min_preparedness.adapter;

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
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.helper.DateHelper;
import org.alertpreparedness.platform.alert.helper.UserInfo;
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
 * Created by faizmohideen on 20/12/2017.
 */

public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ViewHolder> implements ChildEventListener {

    private final ArrayList<String> keys;
    private Context context;
    private HashMap<String, Action> items;
    private DatabaseReference dbRef;
    private ItemSelectedListener listener;
    private String dateFormat = "MMM dd,yyyy";
    private SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());

    @Inject
    @AgencyRef
    DatabaseReference dbAgencyRef;

    //In progress
    public void addInProgressItem(String key, Action action) {
        System.out.println("keys.indexOf(key) = " + key);
        if (keys.indexOf(key) == -1) {
            if (action.getLevel() != null
                    && action.getLevel() == Constants.MPA
                    && action.getDueDate() != null
                    && !DateHelper.itWasDue(action.getDueDate())
                    && action.getTaskName() != null || action.getCHS()) {
                keys.add(key);
                items.put(key, action);
                notifyItemInserted(keys.size()-1);
            }
        } else {
            items.put(key, action);
            notifyItemChanged(keys.indexOf(key));
        }
    }

    public void addExpiredItem(String key, Action action) {
        if (keys.indexOf(key) == -1) {
            if (action.getLevel() != null
                    && action.getLevel() == Constants.MPA
                    && action.getDueDate() != null
                    && DateHelper.itWasDue(action.getDueDate())
                    && action.getTaskName() != null) {
                keys.add(key);
                items.put(key, action);
                notifyItemInserted(keys.size());
            }
        } else {
            items.put(key, action);
            notifyItemChanged(keys.indexOf(key));
        }

    }

    public void addUnassignedItem(String key, Action action) {
        if (keys.indexOf(key) == -1) {
            System.out.println("keys.indexOf(key) = " + keys.indexOf(key));

            if (action.getLevel() != null
                    && action.getLevel() == Constants.MPA
                    && action.getAssignee() == null
                    && action.getTaskName() != null
                    && action.getDueDate() == null) {
                if (action.getActionType() == Constants.MANDATED
                        || action.getActionType() == Constants.CUSTOM) {
                    keys.add(key);
                    items.put(key, action);
                    notifyItemInserted(keys.size());
                }
            }
        } else {
            items.put(key, action);
            notifyItemChanged(keys.indexOf(key));
        }

    }

    public void addCompletedItem(String key, Action action) {
        if (keys.indexOf(key) == -1) {
            if (action.getLevel() != null
                    && action.getLevel() == Constants.MPA
                    && action.getComplete() != null
                    && action.getComplete()
                    && action.getDueDate() != null) {
                keys.add(key);
                items.put(key, action);
                notifyItemInserted(keys.size());
            }
        } else {
            items.put(key, action);
            notifyItemChanged(keys.indexOf(key));
        }
    }

    public void addArchivedItem(String key, Action action) {
        if (keys.indexOf(key) == -1) {
            if (action.getLevel() != null
                    && action.getLevel() == Constants.MPA
                    && action.getArchived() != null
                    && action.getArchived()
                    && action.getDueDate() != null) {
                keys.add(key);
                items.put(key, action);
                notifyItemInserted(keys.size());
            }
        } else {
            items.put(key, action);
            notifyItemChanged(keys.indexOf(key));
        }
    }

    public void addCHSAction(String key, Action action) {
        if (keys.indexOf(key) == -1) {
           if(action.getCHS() && action.getTaskName() == null && action.getLevel() == null){
                keys.add(key);
                items.put(key, action);
                notifyItemInserted(keys.size()-1);
            }
        } else {
            items.put(key, action);
            notifyItemChanged(keys.indexOf(key));
        }
    }


    public ActionAdapter(Context context, DatabaseReference dbRef, ItemSelectedListener listener) {
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

    public void removeItem(String key) {
        int index = keys.indexOf(key);
        items.remove(keys.get(index));
        keys.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public ActionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_minimum_actions, parent, false);
        return new ActionAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ActionAdapter.ViewHolder holder, int position) {
        Action action = items.get(keys.get(position));
        getDepartment(action.db, action.userRef, action.getDepartment(), action.getAssignee(), holder);
        holder.tvEmptyAction.setVisibility(View.GONE);
        holder.tvActionType.setText(getActionType((int) action.getActionType()));
        holder.tvActionName.setText(action.getTaskName());
        holder.tvBudget.setText(getBudget(action.getBudget()));
        holder.tvDueDate.setText(getDate(action.getDueDate()));
        holder.itemView.setOnClickListener((v) -> listener.onActionItemSelected(position, keys.get(position)));

        if (action.getDueDate() == null) {
            holder.tvDueDate.setVisibility(View.GONE);
        }

        if (items.isEmpty()) {
            holder.tvEmptyAction.setVisibility(View.VISIBLE);
            holder.tvActionName.setVisibility(View.GONE);
            holder.tvActionType.setVisibility(View.GONE);
            holder.tvBudget.setVisibility(View.GONE);
            holder.tvDueDate.setVisibility(View.GONE);
            holder.tvUserName.setVisibility(View.GONE);
        }

    }

    private void getDepartment(DatabaseReference db, DatabaseReference userRef, String departmentID, String assignee, ActionAdapter.ViewHolder holder) {

        db.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String department = (String) dataSnapshot.child("departments").child(departmentID).child("name").getValue();
                setUser(holder, userRef, assignee, department);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setUser(ActionAdapter.ViewHolder holder, DatabaseReference userRef, String assignee, String department) {
        if (assignee != null) {
            userRef.child(assignee).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String firstname = (String) dataSnapshot.child("firstName").getValue();
                    String lastname = (String) dataSnapshot.child("lastName").getValue();
                    String fullname = String.format("%s %s", firstname, lastname);
                    holder.tvUserName.setText(fullname + ", " + department);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            holder.tvUserName.setText("Unassigned, "+department);
        }

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

        @BindView(R.id.tvNoAction)
        TextView tvEmptyAction;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private String getBudget(Long budget) {
        return "$" + budget;
    }

    private String getDate(Long date) {
        if (date != null) {
            return "Due: " + format.format(new Date(date));
        } else {
            return "Not Assigned";
        }
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
//        if(items.isEmpty()){
//            return 1;
//        }else {
        return items.size();
        //   }
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        Action action = dataSnapshot.getValue(Action.class);

        if (keys.indexOf(dataSnapshot.getKey()) == -1) {
            if (action.getComplete() != null && action.getComplete() && action.getDueDate() != null) {
                keys.add(dataSnapshot.getKey());
                items.put(dataSnapshot.getKey(), action);
                notifyItemInserted(keys.size() - 1);
            }
        } else {
            items.put(dataSnapshot.getKey(), action);
            notifyItemChanged(keys.indexOf(dataSnapshot.getKey()));
        }

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        Action action = dataSnapshot.getValue(Action.class);
        if (keys.indexOf(dataSnapshot.getKey()) == -1) {
            if (action.getComplete() != null && action.getComplete() && action.getDueDate() != null) {
                keys.add(dataSnapshot.getKey());
                items.put(dataSnapshot.getKey(), action);
                notifyItemInserted(keys.size() - 1);
            }
        } else {
            items.put(dataSnapshot.getKey(), action);
            notifyItemChanged(keys.indexOf(dataSnapshot.getKey()));
        }

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

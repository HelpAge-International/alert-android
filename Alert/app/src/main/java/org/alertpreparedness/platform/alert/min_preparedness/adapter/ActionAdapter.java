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
import org.alertpreparedness.platform.alert.min_preparedness.fragment.InProgressFragment;
import org.alertpreparedness.platform.alert.min_preparedness.interfaces.OnItemsChangedListener;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.min_preparedness.model.DataModel;
import org.alertpreparedness.platform.alert.model.User;

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
    private OnItemsChangedListener changedListener;
    private String dateFormat = "MMM dd,yyyy";
    private SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());

    @Inject
    @AgencyRef
    DatabaseReference dbAgencyRef;

    public void addItems(String key, Action action) {
        if (keys.indexOf(key) == -1) {
            keys.add(key);
            items.put(key, action);
            notifyItemInserted(keys.size() - 1);
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
        getDepartment(action.db, action.userRef, action.networkRef, action.user, action.getId(), action.getDepartment(), action.getNetworkId(), action.getAssignee(), holder);
        holder.tvActionType.setText(getActionType((int) action.getActionType()));
        holder.tvActionName.setText(action.getTaskName());
        holder.tvBudget.setText(getBudget(action.getBudget()));
        holder.tvDueDate.setText(getDate(action.getDueDate()));
        holder.itemView.setOnClickListener((v) -> listener.onActionItemSelected(position, keys.get(position)));

        if (action.getDueDate() == null) {
            holder.tvDueDate.setVisibility(View.GONE);
        }

        if (action.getBudget() == null) {
            holder.tvBudget.setVisibility(View.GONE);
        }

        if (items.isEmpty()) {
            holder.tvActionName.setVisibility(View.GONE);
            holder.tvActionType.setVisibility(View.GONE);
            holder.tvBudget.setVisibility(View.GONE);
            holder.tvDueDate.setVisibility(View.GONE);
            holder.tvUserName.setVisibility(View.GONE);
        }

    }

    private void getDepartment(DatabaseReference db, DatabaseReference userRef, DatabaseReference networkRef, User user, String id, String departmentID, String networkID, String assignee, ActionAdapter.ViewHolder holder) {

        db.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (departmentID != null) {
                    String department = (String) dataSnapshot.child("departments").child(departmentID).child("name").getValue();
                    setUser(holder, userRef, assignee, department);
                } else if (networkID != null){
                    System.out.println("networkID = " + networkID);
                    setNetworkUser(holder, userRef, networkRef, assignee, networkID, user);
                } else if (id.equals(user.getLocalNetworkID())){
                    setLocalNetworkUser(holder, userRef, networkRef, assignee, id, user);
                }
                else {
                    setUser(holder, userRef, null, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setLocalNetworkUser(ViewHolder holder, DatabaseReference userRef, DatabaseReference networkRef, String assignee, String id, User user) {
        networkRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = (String) dataSnapshot.child("name").getValue();
                System.out.println("name IN LOCAL = " + name);
                userRef.child(assignee).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String firstname = (String) dataSnapshot.child("firstName").getValue();
                        String lastname = (String) dataSnapshot.child("lastName").getValue();
                        String fullname = String.format("%s %s", firstname, lastname);
                        holder.tvUserName.setText(fullname + ", " + name);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setNetworkUser(ViewHolder holder, DatabaseReference userRef, DatabaseReference networkRef, String assignee, String networkID, User user) {
        if (assignee != null && networkID != null) {
            //System.out.println("user.getLocalNetworkID() = " + user.getLocalNetworkID());
            networkRef.child(networkID).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = (String) dataSnapshot.child("name").getValue();

                    userRef.child(assignee).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String firstname = (String) dataSnapshot.child("firstName").getValue();
                            String lastname = (String) dataSnapshot.child("lastName").getValue();
                            String fullname = String.format("%s %s", firstname, lastname);
                            holder.tvUserName.setText(fullname + ", " + name);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private void setUser(ActionAdapter.ViewHolder holder, DatabaseReference userRef, String assignee, String department) {
        System.out.println("department = " + department);
        if (assignee != null && department != null) {
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
        }
//        else {
//            holder.tvUserName.setText("Unassigned");
//        }

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
        return items.size();
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
      //  changedListener.onItemChanged(dataSnapshot);
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

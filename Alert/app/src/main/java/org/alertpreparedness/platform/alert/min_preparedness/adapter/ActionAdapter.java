package org.alertpreparedness.platform.alert.min_preparedness.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.CountryOfficeRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.alert.firebase.ActionModel;
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

public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ViewHolder> {

    private final ArrayList<String> keys;
    private Context context;
    private HashMap<String, ActionModel> items;
    private ActionAdapterListener listener;
    private String dateFormat = "MMM dd,yyyy";
    private SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());

    @Inject
    @AgencyRef
    DatabaseReference dbAgencyRef;

    @Inject
    User user;

    @Inject
    @CountryOfficeRef
    DatabaseReference countryOfficeRef;

    @Inject
    @BaseActionRef
    public DatabaseReference dbRef;

    @Inject
    @UserPublicRef
    DatabaseReference userPublicRef;




    public void addItems(String key, ActionModel action) {
        if (keys.indexOf(key) == -1) {
            keys.add(key);
            items.put(key, action);
            notifyItemInserted(keys.size() - 1);
        } else {
            items.put(key, action);
            notifyItemChanged(keys.indexOf(key));
        }
    }

    public void updateKeys(ArrayList<String> newKeys) {
        ArrayList<String> itemsToRemove = new ArrayList<>();
        for(String oldKey : this.keys) {
            if(!newKeys.contains(oldKey)) {
                itemsToRemove.add(oldKey);
            }
        }

        for (String key : itemsToRemove) {
            int index = this.keys.indexOf(key);
            String oldKey = this.keys.get(index);
            this.items.remove(oldKey);
            this.keys.remove(index);
            listener.itemRemoved(key);
            notifyItemRemoved(index);

        }
    }

    public ActionAdapter(Context context, ActionAdapterListener listener) {
        this.context = context;
        this.items = new HashMap<>();
        this.listener = listener;
        this.listener = listener;
        this.keys = new ArrayList<>(items.keySet());
        DependencyInjector.userScopeComponent().inject(this);
    }

    public ActionModel getItem(int index) {
        return items.get(keys.get(index));
    }

    public ActionModel getItem(String key) {
        return items.get(key);
    }

    public void removeItem(String key) {
        int index = keys.indexOf(key);
        if(index != -1) {
            items.remove(keys.get(index));
            keys.remove(index);
            notifyItemRemoved(index);
            listener.itemRemoved(key);
        }
    }

    @Override
    public ActionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_minimum_actions, parent, false);
        return new ActionAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ActionAdapter.ViewHolder holder, int position) {
        ActionModel action = items.get(keys.get(position));
        getDepartment(action.getDepartment(), action.getParentId(), action.getAsignee(), holder);
//        getDepartment(action.db, action.userRef, action.networkRef, action.user, action.getId(), action.getDepartment(), action.getNetworkId(), action.getAsignee(), holder);

        holder.tvActionType.setText(getActionType(action.getType().intValue()));
        holder.tvActionName.setText(action.getTask());
        holder.tvBudget.setText(getBudget(action.getBudget()));
        holder.tvDueDate.setText(getDate(action.getDueDate()));
        holder.itemView.setOnClickListener((v) -> listener.onActionItemSelected(position, action.getId(), action.getParentId()));

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

    private void getDepartment(String departmentID, String networkID, String assignee, ActionAdapter.ViewHolder holder) {

        dbAgencyRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (assignee != null) {
                    String department = null;
                    if(departmentID != null) {
                        department = (String) dataSnapshot.child("departments").child(departmentID).child("name").getValue();
                    }
//                    if(department == null) {
//                        getCountryDepartment(holder, userPublicRef, assignee, departmentID);
//                    }
                    setUser(holder, userPublicRef, assignee, department);
                }
//                else if (assignee!=null && networkID != null && networkID.equals(user.getNetworkID())){
//                    setNetworkUser(holder, userPublicRef, networkRef, assignee, networkID, user);
//                } else if (assignee!=null && id != null && id.equals(user.getLocalNetworkID())){
//                    setLocalNetworkUser(holder, userPublicRef, networkRef, assignee, id, user);
//                } else if (assignee!=null && id != null && id.equals(user.getNetworkCountryID())){
//                    setLocalNetworkUser(holder, userPublicRef, networkRef, assignee, id, user);
//                }
                else {
                    holder.tvUserName.setText("Unassigned");
                   // setUser(holder, userRef, null, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getCountryDepartment(ActionAdapter.ViewHolder holder, DatabaseReference userRef, String assignee, String departmentID) {
        countryOfficeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String department = (String) dataSnapshot.child("departments").child(departmentID).child("name").getValue();
                System.out.println("department2 = " + department);

                setUser(holder, userRef, assignee, department);

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

    private void setUser(ActionAdapter.ViewHolder holder, DatabaseReference userRef, String assignee, String department) {
        System.out.println("department = " + department);
            userRef.child(assignee).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String firstname = (String) dataSnapshot.child("firstName").getValue();
                    String lastname = (String) dataSnapshot.child("lastName").getValue();
                    String fullname = String.format("%s %s", firstname, lastname);
                    holder.tvUserName.setText(department == null ? fullname : fullname + ", " + department);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
//
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


    public interface ActionAdapterListener {
        void onActionItemSelected(int pos, String key, String parentId);

        void itemRemoved(String key);
    }


}

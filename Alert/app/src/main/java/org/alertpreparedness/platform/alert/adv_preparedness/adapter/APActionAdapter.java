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
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.CountryOfficeRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.alert.firebase.ActionModel;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.PreparednessAdapter;
import org.alertpreparedness.platform.alert.utils.AppUtils;
import org.alertpreparedness.platform.alert.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faizmohideen on 05/01/2018.
 */

public class APActionAdapter extends RecyclerView.Adapter<APActionAdapter.ViewHolder> implements ChildEventListener, PreparednessAdapter {

    private final ArrayList<String> keys;
    private HashMap<String, ActionModel> items;
    private APAAdapterListener listener;
    private String dateFormat = "MMM dd,yyyy";
    private SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());

    @Inject
    @BaseActionRef
    DatabaseReference dbRef;

    @Inject
    @UserPublicRef
    DatabaseReference userPublic;

    @Inject
    @CountryOfficeRef
    DatabaseReference countryRef;

    @Inject
    @AgencyRef
    public DatabaseReference dbAgencyRef;

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

    public void bindChildListeners(List<String> ids) {
//        for (String id : ids) {
//            dbRef.child(id).addChildEventListener(this);
//        }
    }

    public APActionAdapter(Context context, APAAdapterListener listener) {
        this.items = new HashMap<>();
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
            listener.onAdapterItemRemoved(key);
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
            notifyItemRemoved(index);

        }
    }


    @Override
    public APActionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_minimum_actions, parent, false);
        return new APActionAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(APActionAdapter.ViewHolder holder, int position) {
        ActionModel action = items.get(keys.get(position));

        try {
            getDepartment(action.getDepartment(), action.getAsignee(), holder);

            holder.tvActionType.setText(getActionType(action.getType()));
            holder.tvActionName.setText(action.getTask());
            holder.tvBudget.setText(getBudget(action.getBudget()));
            holder.tvDueDate.setText(getDate(action.getDueDate()));
            holder.itemView.setOnClickListener((v) -> listener.onActionItemSelected(position, keys.get(position), action.getId()));

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
        } catch (Exception e) {
            System.out.println("e = " + e);
            e.printStackTrace();
        }

    }

    private void getDepartment(String departmentID, String assignee, APActionAdapter.ViewHolder holder) {
        dbAgencyRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (departmentID != null) {
                    String department = (String) dataSnapshot.child("departments").child(departmentID).child("name").getValue();
                    if(department == null) {
                           countryRef.child("departments").child(departmentID).addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot dataSnapshot) {
                                   String department = (String) dataSnapshot.child("name").getValue();
                                   setUser(holder, assignee, department);
                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {

                               }
                           });
                    }
                    else {
                        setUser(holder, assignee, department);
                    }
                } else {
                    setUser(holder, assignee, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setUser(APActionAdapter.ViewHolder holder, String assignee, String department) {
        if (assignee != null) {
            userPublic.child(assignee).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String firstname = (String) dataSnapshot.child("firstName").getValue();
                    String lastname = (String) dataSnapshot.child("lastName").getValue();
                    String fullname = String.format("%s %s", firstname, lastname);
                    if(department == null) {
                        holder.tvUserName.setText(String.format("%s", fullname));

                    }
                    else {
                        holder.tvUserName.setText(String.format("%s, %s", fullname, department));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            holder.tvUserName.setText("Unassigned");
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
            case Constants.CHS:
                return "CHS";
            case Constants.MANDATED:
                return "Mandated";
            case Constants.CUSTOM:
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

        ActionModel action = AppUtils.getFirebaseModelFromDataSnapshot(dataSnapshot, ActionModel.class);

        assert action != null;
        if (keys.indexOf(dataSnapshot.getKey()) == -1) {
            if (action.getIsComplete() && action.getDueDate() != null) {
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
        ActionModel action = AppUtils.getFirebaseModelFromDataSnapshot(dataSnapshot, ActionModel.class);
        assert action != null;

        if (keys.indexOf(dataSnapshot.getKey()) == -1) {
            if (action.getIsComplete() && action.getDueDate() != null) {
                keys.add(dataSnapshot.getKey());
                items.put(dataSnapshot.getKey(), action);
                notifyItemInserted(keys.size() - 1);
            }
        }
        else {
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

    public interface APAAdapterListener {
        void onActionItemSelected(int pos, String key, String parentId);
        void onAdapterItemRemoved(String key);
    }
}

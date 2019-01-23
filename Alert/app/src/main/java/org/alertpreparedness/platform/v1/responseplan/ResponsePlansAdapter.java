package org.alertpreparedness.platform.v1.responseplan;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.v1.R;
import org.alertpreparedness.platform.v1.min_preparedness.model.Note;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tj on 12/12/2017.
 */

public class ResponsePlansAdapter extends RecyclerView.Adapter<ResponsePlansAdapter.ViewHolder> implements ChildEventListener {

    private final ArrayList<String> keys;
    private Context context;
    private boolean shouldBeActive;
    private HashMap<String, ResponsePlanObj> items;
    private DatabaseReference responsePlans;
    private ResponseAdapterListener listner;

    public void addItem(String key, ResponsePlanObj responsePlanObj) {
        if(keys.indexOf(key) == -1) {
            keys.add(key);
            items.put(key, responsePlanObj);
            notifyItemInserted(keys.size()-1);
        }
        else {
            items.put(key, responsePlanObj);
            notifyItemChanged(keys.indexOf(key));
        }
    }

    public ResponsePlanObj getItem(int index) {
        return items.get(keys.get(index));
    }

    public ResponsePlanObj getItem(String key) {
        return items.get(key);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        //handled by activity
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        //handled by activity
        boolean a = (boolean)dataSnapshot.child("isActive").getValue();
        if(a != shouldBeActive) {
            removeItem(dataSnapshot);
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        removeItem(dataSnapshot);
    }

    public void removeItem(DataSnapshot dataSnapshot) {
        int index = keys.indexOf(dataSnapshot.getKey());
        if(index != -1) {
            notifyItemRemoved(index);
            items.remove(dataSnapshot.getKey());
            keys.remove(index);
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public void addNote(String planKey, Note note) {
        if(items.get(planKey) != null) {
            items.get(planKey).addNote(note.getId(), note);
        }
    }

    public void removeNote(String planKey, String key) {
        if(items.get(planKey) != null) {
            items.get(planKey).removeNote(key);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.vLine)
        View line;

        @BindView(R.id.ivStatusIcon)
        ImageView statusIcon;

        @BindView(R.id.tvHazardType)
        TextView hazardType;

        @BindView(R.id.tvPercentComplete)
        TextView percentComplete;

        @BindView(R.id.tvDescription)
        TextView description;

        @BindView(R.id.tvStatus)
        TextView status;

        @BindView(R.id.tvLastUpdated)
        TextView lastUpdated;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


    public ResponsePlansAdapter(Context context, DatabaseReference responsePlans, boolean shouldBeActive, ResponseAdapterListener listner) {
        this.context = context;
        this.shouldBeActive = shouldBeActive;
        this.items = new HashMap<>();
        this.keys = new ArrayList<>(items.keySet());
        this.responsePlans = responsePlans;
        this.listner = listner;
        this.responsePlans.addChildEventListener(this);
    }

    @Override
    public ResponsePlansAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_response_plan, parent, false);
        return new ResponsePlansAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ResponsePlansAdapter.ViewHolder holder, int position) {

        ResponsePlanObj model = items.get(keys.get(position));

        holder.description.setText(model.description);
        holder.percentComplete.setText(String.format(context.getString(R.string.complete), (Integer.parseInt(model.completePercentage) * 10)));
        holder.hazardType.setText(model.hazardType);
        holder.lastUpdated.setText(String.format(context.getString(R.string.last_updated_f), DateFormat.getDateInstance(DateFormat.SHORT).format(model.lastUpdated)));
        switch (model.status) {
            case 1://grey
                holder.line.setBackgroundColor(context.getResources().getColor(R.color.alertGray));
                holder.status.setTextColor(context.getResources().getColor(R.color.alertGray));
                holder.status.setText(R.string.waiting_approval);
                holder.statusIcon.setImageResource(R.drawable.icon_pending_grey);
                break;
            case 2://green
                holder.line.setBackgroundColor(context.getResources().getColor(R.color.alertGreen));
                holder.status.setTextColor(context.getResources().getColor(R.color.alertGreen));
                holder.status.setText(R.string.approved);
                holder.statusIcon.setImageResource(R.drawable.icon_status_complete);
                break;
            case 0://amber
                holder.line.setBackgroundColor(context.getResources().getColor(R.color.alertAmber));
                holder.status.setTextColor(context.getResources().getColor(R.color.alertAmber));
                holder.status.setText(R.string.in_progress);
                holder.statusIcon.setImageResource(R.drawable.icon_pending_amber);
                break;
            case 3:
                holder.line.setBackgroundColor(context.getResources().getColor(R.color.alertRed));
                holder.status.setTextColor(context.getResources().getColor(R.color.alertRed));
                holder.status.setText(R.string.needs_reviewing);
                holder.statusIcon.setImageResource(R.drawable.icon_status_needs_reviewing);
                break;
        }
        holder.itemView.setOnClickListener((v) -> listner.onResponsePlanSelected(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface ResponseAdapterListener {
        void onResponsePlanSelected(int pos);
    }
}

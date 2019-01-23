package org.alertpreparedness.platform.v1.min_preparedness.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.v1.R;
import org.alertpreparedness.platform.v1.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.v1.min_preparedness.model.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faizmohideen on 03/01/2018.
 */

public class AddNotesAdapter extends RecyclerView.Adapter<AddNotesAdapter.ViewHolder> implements ChildEventListener {

    @Inject
    @UserPublicRef
    DatabaseReference dbUserPublicRef;

    private HashMap<String, Note> items;
    private final ArrayList<String> keys;
    private Context context;
    private DatabaseReference dbRef;
    private AddNotesAdapter.ItemSelectedListener listener;
    private String dateFormat = "dd MMMM yyyy";
    private SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());

    public AddNotesAdapter(Context context, DatabaseReference dbRef, AddNotesAdapter.ItemSelectedListener listener) {
        this.context = context;
        this.items = new HashMap<>();
        this.dbRef = dbRef;
        this.listener = listener;
        this.keys = new ArrayList<>(items.keySet());
        this.dbRef.addChildEventListener(this);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvLogWriterName)
        TextView tvName;

        @BindView(R.id.tvLogContent)
        TextView tvContent;

        @BindView(R.id.tvLogDate)
        TextView tvDate;

        @BindView(R.id.tvLogStatus)
        TextView tvStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tvStatus.setVisibility(View.GONE);
        }
    }

    public void addInProgressItem(String key, Note notes) {
        if (keys.indexOf(key) == -1) {
            keys.add(key);
            items.put(key, notes);
            notifyItemInserted(keys.size() - 1);
            listener.onNewITemAdded();
        } else {
            items.put(key, notes);
            notifyItemChanged(keys.indexOf(key));
        }
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Note note = dataSnapshot.getValue(Note.class);
        if(keys.indexOf(dataSnapshot.getKey()) == -1) {
            keys.add(dataSnapshot.getKey());
        }
        items.put(dataSnapshot.getKey(), note);
        notifyItemInserted(keys.indexOf(dataSnapshot.getKey()));
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Note note = dataSnapshot.getValue(Note.class);
        if(keys.indexOf(dataSnapshot.getKey()) == -1) {
            keys.add(dataSnapshot.getKey());
        }
        items.put(dataSnapshot.getKey(), note);
        notifyDataSetChanged();
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        int index = keys.indexOf(dataSnapshot.getKey());
        items.remove(dataSnapshot.getKey());
        keys.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public AddNotesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_indicator_log, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AddNotesAdapter.ViewHolder holder, int position) {
        Note note = items.get(keys.get(position));
        holder.tvName.setText(note.getUploadBy());
        holder.tvContent.setText(note.getContent());
        holder.tvDate.setText(format.format(new Date(note.getTime())));
        holder.itemView.setOnClickListener((v) -> listener.onNoteItemSelected(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface ItemSelectedListener {
        void onNoteItemSelected(int pos);
        void onNewITemAdded();
    }

}

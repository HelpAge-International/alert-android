package org.alertpreparedness.platform.v1.min_preparedness.activity;

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

import org.alertpreparedness.platform.v1.R;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.v1.firebase.DocumentModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Tj on 21/02/2018.
 */

public class ViewAttachmentAdapter extends RecyclerView.Adapter<ViewAttachmentAdapter.ViewHolder> {

    private final ArrayList<String> keys = new ArrayList<>();
    private Context context;
    private HashMap<String, DocumentModel> items = new HashMap<>();

    @Inject
    @UserPublicRef
    DatabaseReference userRef;

    @Inject
    SimpleDateFormat dateFormetter;
    private AttachementSelectListener listener;

    public ViewAttachmentAdapter(Context context, AttachementSelectListener listener) {
        this.listener = listener;
        DependencyInjector.userScopeComponent().inject(this);
        this.context = context;
    }

    public void addItem(DocumentModel model) {
        if (keys.indexOf(model.getKey()) == -1) {
            keys.add(model.getKey());
            items.put(model.getKey(), model);
            notifyItemInserted(keys.size() - 1);
        } else {
            items.put(model.getKey(), model);
            notifyItemChanged(keys.indexOf(model.getKey()));
        }
    }

    public void removeItem(String key) {
        int index = keys.indexOf(key);
        if(index != -1) {
            items.remove(keys.get(index));
            keys.remove(index);
            notifyItemRemoved(index);
        }
    }

    public DocumentModel getItem(int index) {
        return items.get(keys.get(index));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attachment, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DocumentModel document = items.get(keys.get(position));

        holder.file.setText(document.getFileName());
        holder.date.setText(dateFormetter.format(new Date(document.getTime())));
        getUploadedBy(document.getUploadedBy(), holder);

    }

    public void getUploadedBy(String userKey, ViewHolder holder) {
        userRef.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.title.setText(String.format("%s %s", dataSnapshot.child("firstName").getValue(String.class), dataSnapshot.child("lastName").getValue(String.class)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;

        @BindView(R.id.file)
        TextView file;

        @BindView(R.id.date)
        TextView date;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick
        public void onClick(View v) {
            listener.onAttachmentSelected(items.get(keys.get(getAdapterPosition())));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface AttachementSelectListener {
        void onAttachmentSelected(DocumentModel document);
    }
}

package org.alertpreparedness.platform.alert.responseplan;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.min_preparedness.model.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tj on 13/03/2018.
 */

public class ApprovalNotesAdapter extends RecyclerView.Adapter<ApprovalNotesAdapter.ViewHolder> {

    private List<Note> values;

    @Inject
    SimpleDateFormat simpleDateFormat;

    public ApprovalNotesAdapter(List<Note> values) {
        this.values = values;
        DependencyInjector.userScopeComponent().inject(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_approval_note, parent, false);
        return new ApprovalNotesAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.by.setText(values.get(position).getFullName());
        holder.date.setText(simpleDateFormat.format(new Date(values.get(position).getTime())));
        holder.note.setText(values.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.note)
        TextView note;

        @BindView(R.id.by)
        TextView by;

        @BindView(R.id.date)
        TextView date;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

package org.alertpreparedness.platform.alert.min_preparedness.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faizmohideen on 29/12/2017.
 */

public class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.ViewHolder> {
    @Override
    public AttachmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false);
        return new AttachmentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
       // holder.rvComAttachments.setVisibility(View.VISIBLE);
        holder.txtAttachment.setText(R.string.txt_attachments);
    }




    @Override
    public int getItemCount() {
        return 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.rvCompletionAttachments)
//        RecyclerView rvComAttachments;

        @BindView(R.id.textview)
        TextView txtAttachment;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
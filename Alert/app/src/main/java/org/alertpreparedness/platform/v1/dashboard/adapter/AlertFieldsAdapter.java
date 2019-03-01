package org.alertpreparedness.platform.v1.dashboard.adapter;

import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.alertpreparedness.platform.R;
import org.alertpreparedness.platform.v1.dashboard.model.AlertFieldModel;
import org.alertpreparedness.platform.v1.utils.SimpleAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tj on 06/12/2017.
 */

public
class AlertFieldsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SimpleAdapter.RemoveListener {

    private Context context;
    public final List<AlertFieldModel> items;
    private ClickListener listener;
    public final static int TEXT_FIELD = 0;
    public final static int EDIT_TEXT = 1;
    public final static int RECYCLER = 2;
    private boolean isRedAlert;

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvField)
        public TextView field;

        @BindView(R.id.ivIcon)
        public ImageView image;

        @BindView(R.id.recyclerCon)
        LinearLayout recylclerCon;

        @BindView(R.id.text)
        public TextView textView;

        @BindView(R.id.recycler)
        RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {

        @BindView(R.id.tvField)
        public EditText field;

        @BindView(R.id.ivIcon)
        public ImageView image;

        public ViewHolder1(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public AlertFieldsAdapter(Context context, List<AlertFieldModel> models, ClickListener listener) {
        this.context = context;
        this.items = models;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == EDIT_TEXT) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_create_alert_edit_field, parent, false);
            return new ViewHolder1(itemView);
        }
        else /*if(viewType == TEXT_FIELD)*/ {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_create_alert_field, parent, false);
            return new ViewHolder(itemView);

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        AlertFieldModel m = items.get(position);

        if(m.originalPosition == -1) {
            m.originalPosition = holder.getAdapterPosition();
        }

        switch (getItemViewType(position)) {
            case TEXT_FIELD:
                ViewHolder h = (ViewHolder)holder;
                if(m.resultTitle != null) {
                    h.field.setText(m.resultTitle);
                    h.field.setTextColor(context.getResources().getColor(android.R.color.black));
                }
                else {
                    h.field.setText(m.initialTitle);
                }
                h.image.setImageDrawable(ContextCompat.getDrawable(context, m.drawable));
                h.field.setOnClickListener(view -> listener.onItemClicked(m.originalPosition));
                h.recylclerCon.setVisibility(View.GONE);
                break;
            case EDIT_TEXT:
                ViewHolder1 h1 = (ViewHolder1)holder;

                if(m.resultTitle != null) {
                    h1.field.setText(m.resultTitle);
                }
                else {
                    h1.field.setHint(m.initialTitle);
                }

                h1.field.setInputType(m.inputType);

                h1.image.setImageDrawable(
                        ContextCompat.getDrawable(context, m.drawable)
                );

                h1.field.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        System.out.println(context.getString(m.initialTitle) + " " + editable.toString());
                        m.resultTitle = editable.toString();
                    }
                });


                break;
            case RECYCLER:
                ViewHolder h2 = (ViewHolder)holder;
                h2.field.setVisibility(View.GONE);
                h2.recylclerCon.setVisibility(View.VISIBLE);

                if(m.strings == null || m.strings.size() == 0) {
                    h2.recyclerView.setVisibility(View.GONE);
                }
                else {
                    h2.recyclerView.setVisibility(View.VISIBLE);
                    h2.recyclerView.setAdapter(new SimpleAdapter(m.originalPosition, m.strings, this));
                    h2.recyclerView.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
                    h2.recyclerView.setHasFixedSize(true);
                    h2.recyclerView.setNestedScrollingEnabled(false);

                    h2.textView.setText(R.string.add_another_area);

                }
                h2.image.setImageDrawable(
                        ContextCompat.getDrawable(context, m.drawable)
                );
                h2.textView.setOnClickListener(view -> listener.onItemClicked(m.originalPosition));
                break;
        }

    }

    @Override
    public void onItemRemove(int positionInParent, int position) {
        positionInParent = (isRedAlert && positionInParent >= 2? positionInParent + 1 : positionInParent);
        items.get(positionInParent).strings.remove(position);
        notifyItemChanged(positionInParent);
        listener.onSubItemRemoved(positionInParent, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).viewType;
    }

    public void addRedAlertReason() {
        if(!isRedAlert) {
            items.add(2, new AlertFieldModel(EDIT_TEXT, R.drawable.alert_red_reason, R.string.red_trigger_name));
            isRedAlert = true;
            notifyItemInserted(2);
        }
    }

    public String getRedAlertReason() {
        if(isRedAlert()) {
            return items.get(2).resultTitle;
        }
        return null;
    }

    public void removeRedReason() {
        if(isRedAlert) {
            items.remove(2);
            isRedAlert = false;
            notifyItemRemoved(2);
        }
    }

    public void addRedAlertReason(String reason) {
        if(!isRedAlert) {
            AlertFieldModel m = new AlertFieldModel(EDIT_TEXT, R.drawable.alert_red_reason, R.string.red_trigger_name);
            m.resultTitle = reason;
            items.add(2, m);
            isRedAlert = true;
            notifyItemInserted(2);
        }
    }

    public void setTextFieldValue(int index, String string) {
        index = (isRedAlert && index >= 2? index + 1 : index);
        items.get(index).resultTitle = string;
        notifyItemChanged(index);
    }
    public void setTextFieldValue(int index, @DrawableRes int icon, String string) {
        index = (isRedAlert && index >= 2? index + 1 : index);
        items.get(index).resultTitle = string;
        items.get(index).drawable = icon;
        notifyItemChanged(index);
    }

    public void addSubListValue(int index, String string) {
        index = (isRedAlert && index >= 2? index + 1 : index);
        AlertFieldModel m = items.get(index);
        if(m.strings != null) {
            m.strings.add(string);
        }
//        notifyItemChanged(index);
        notifyDataSetChanged();
    }

    public int getSubListCapacity(int index){
        index = (isRedAlert && index >= 2? index + 1 : index);
        AlertFieldModel m = items.get(index);
        return m.strings.size();
    }

    public AlertFieldModel getModel(int i) {
        return items.get(i);
    }

    public boolean isRedAlert() {
        return isRedAlert;
    }

    public interface ClickListener {

        void onItemClicked(int position);

        void onSubItemRemoved(int positionInParent, int position);

    }
}





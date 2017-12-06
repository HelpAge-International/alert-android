package org.alertpreparedness.platform.alert.dashboard.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.helper.AlertLevelDialog;
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelIndicatorLocation;
import org.alertpreparedness.platform.alert.risk_monitoring.view.SelectAreaActivity;
import org.alertpreparedness.platform.alert.utils.SimpleAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateAlertActivity extends AppCompatActivity implements ClickListener, AlertLevelDialog.TypeSelectedListener {

    private static final int ALERT_TYPE_REQ = 9001;
    private static final int EFFECTED_AREA_REQUEST = 9002;

    @BindView(R.id.btnSaveChanges)
    Button saveButton;

    @BindView(R.id.rvFields)
    RecyclerView fields;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private AlertLevelDialog mAlertLevelFragment;
    private AlertFieldsAdapter mFieldsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alert);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ArrayList<AlertFieldModel> list = new ArrayList<>();
        list.add(new AlertFieldModel(AlertFieldsAdapter.TEXT_FIELD, R.drawable.alert_hazard, R.string.select_hazard));
        list.add(new AlertFieldModel(AlertFieldsAdapter.TEXT_FIELD, R.drawable.alert_base, R.string.alert_level));
        list.add(new AlertFieldModel(AlertFieldsAdapter.EDIT_TEXT, R.drawable.alert_population, R.string.estimated_peeps, InputType.TYPE_CLASS_NUMBER));
        list.add(new AlertFieldModel(AlertFieldsAdapter.RECYCLER, R.drawable.alert_areas, R.string.effected_area, null));
        list.add(new AlertFieldModel(AlertFieldsAdapter.EDIT_TEXT, R.drawable.alert_information, R.string.info_sources));

        mFieldsAdapter = new AlertFieldsAdapter(this, list, this);
        fields.setAdapter(mFieldsAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        mLayoutManager.setAutoMeasureEnabled(true);
        fields.setLayoutManager(mLayoutManager);
        fields.setNestedScrollingEnabled(false);

        mAlertLevelFragment = new AlertLevelDialog();
        mAlertLevelFragment.setListener(this);

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onItemClicked(int position) {
        System.out.println("position = [" + position + "]");
        switch (position) {
            case 0:
                startActivity(new Intent(this, HazardSelectionActivity.class));
                break;
            case 1:
                mAlertLevelFragment.show(getSupportFragmentManager(), "alert_level");
                break;
            case 3:
                startActivityForResult(new Intent(this, SelectAreaActivity.class), EFFECTED_AREA_REQUEST);
                break;
        }
    }

    @OnClick(R.id.btnSaveChanges)
    public void onSaveClicked(View v) {
        if(mFieldsAdapter.isRedAlert()) {

        }
        else {

        }

//        Log.d("result123", mFieldsAdapter.getModel(2).resultTitle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EFFECTED_AREA_REQUEST) {
            if (resultCode == RESULT_OK) {
                ModelIndicatorLocation area = data.getParcelableExtra("selected_area");
                String displayable = data.getStringExtra("selected_area_text");

                mFieldsAdapter.addSubListValue(
                        3,
                        displayable
                );
            }
        }
    }

    @Override
    public void onTypeSelected(int type) {
        @DrawableRes int icon;
        String title;
        switch (type) {
            case 1:
                mFieldsAdapter.removeRedReason();
                icon = R.drawable.alert_amber_icon;
                title = getString(R.string.amber_alert_text);
                break;
            case 2:
                icon = R.drawable.alert_red_icon;
                title = getString(R.string.red_alert_text);
                break;
            default:
                mFieldsAdapter.removeRedReason();
                icon = R.drawable.alert_green_icon;
                title = getString(R.string.text_green);
                break;
        }
        mFieldsAdapter.setTextFieldValue(1, icon, title);
        if(type == 2) {
            mFieldsAdapter.addRedAlertReason();
            saveButton.setText(R.string.request_red_alert);
        }
        else {
            saveButton.setText(R.string.confirm_alert_level);
        }
    }
}

class AlertFieldsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SimpleAdapter.RemoveListener {

    private Context context;
    private final List<AlertFieldModel> items;
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
            m.originalPosition = position;
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
                        m.resultTitle = editable.toString();
                    }
                });
                //no needed because edit_text
//                holder.itemView.setOnClickListener(view -> listener.onItemClicked(position));
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
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
                    h2.recyclerView.setLayoutManager(mLayoutManager);
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

    public AlertFieldModel getModel(int i) {
        return items.get(i);
    }

    public boolean isRedAlert() {
        return isRedAlert;
    }
}

class AlertFieldModel {

    public int originalPosition = -1;
    public int viewType;
    @DrawableRes public int drawable;
    @StringRes public int initialTitle;
    public List<String> strings = new ArrayList<>();
    public String resultTitle;
    public int inputType = InputType.TYPE_CLASS_TEXT;

    public AlertFieldModel(int viewType, int drawable, int title) {
        this.viewType = viewType;

        this.drawable = drawable;
        this.initialTitle = title;
    }

    public AlertFieldModel(int viewType, int drawable, int title, int inputType) {
        this.viewType = viewType;
        this.drawable = drawable;
        this.initialTitle = title;
        this.inputType = inputType;
    }

    public AlertFieldModel(int viewType, int drawable, int title, List<String> strings) {
        this.viewType = viewType;
        this.drawable = drawable;
        this.initialTitle = title;
        if(strings != null) {
            this.strings = strings;
        }
    }
}

interface ClickListener {

    void onItemClicked(int position);

}


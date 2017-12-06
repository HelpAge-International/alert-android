package org.alertpreparedness.platform.alert.dashboard.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.utils.CustomLinearLayoutManager;
import org.alertpreparedness.platform.alert.utils.SimpleAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateAlertActivity extends AppCompatActivity implements ClickListener {

    @BindView(R.id.rvFields)
    RecyclerView fields;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alert);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayList<String> areas = new ArrayList<>();
        areas.add("first");
        areas.add("second");
        areas.add("second");
        areas.add("second");
        areas.add("second");
        areas.add("second");

        ArrayList<AlertFieldModel> list = new ArrayList<>();
        list.add(new AlertFieldModel(AlertFieldsAdapter.TEXT_FIELD, R.drawable.alert_hazard, R.string.select_hazard));
        list.add(new AlertFieldModel(AlertFieldsAdapter.TEXT_FIELD, R.drawable.alert_base, R.string.alert_level));
        list.add(new AlertFieldModel(AlertFieldsAdapter.EDIT_TEXT, R.drawable.alert_population, R.string.estimated_peeps));
        list.add(new AlertFieldModel(AlertFieldsAdapter.RECYCLER, R.drawable.alert_areas, R.string.effected_area, areas));
        list.add(new AlertFieldModel(AlertFieldsAdapter.EDIT_TEXT, R.drawable.alert_information, R.string.info_sources));

        fields.setAdapter(new AlertFieldsAdapter(this, list, this));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        mLayoutManager.setAutoMeasureEnabled(true);
        fields.setLayoutManager(mLayoutManager);
        fields.setNestedScrollingEnabled(false);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onItemClicked(int position) {
        switch (position) {
            case 0:
                startActivity(new Intent(this, HazardSelectionActivity.class));
                break;
            case 1:
//                startActivityForResult(new Intent(this, ));
                break;
        }
    }
}

class AlertFieldsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private final List<AlertFieldModel> items;
    private ClickListener listener;
    public final static int TEXT_FIELD = 0;
    public final static int EDIT_TEXT = 1;
    public final static int RECYCLER = 2;

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvField)
        public TextView field;

        @BindView(R.id.ivIcon)
        public ImageView image;

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

    public class ViewHolder2 extends RecyclerView.ViewHolder {

        @BindView(R.id.field)
        public RecyclerView field;

        @BindView(R.id.text)
        public TextView textView;

        @BindView(R.id.ivIcon)
        public ImageView image;

        public ViewHolder2(View itemView) {
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
        if(viewType == TEXT_FIELD) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_create_alert_field, parent, false);
            return new ViewHolder(itemView);

        }
        else if(viewType == EDIT_TEXT) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_create_alert_edit_field, parent, false);
            return new ViewHolder1(itemView);
        }
        else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_create_alert_list_field, parent, false);
            return new ViewHolder2(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)) {
            case TEXT_FIELD:
                ViewHolder h = (ViewHolder)holder;
                h.field.setText(items.get(position).title);
                h.image.setImageDrawable(
                        context.getResources().getDrawable(items.get(position).drawable)
                );
                h.field.setOnClickListener(view -> listener.onItemClicked(position));
                break;
            case EDIT_TEXT:
                ViewHolder1 h1 = (ViewHolder1)holder;
                h1.field.setHint(items.get(position).title);

                if(position == 2) {
                    h1.field.setInputType(InputType.TYPE_CLASS_NUMBER);
                }

                h1.image.setImageDrawable(
                        context.getResources().getDrawable(items.get(position).drawable)
                );
                //no needed because edit_text
//                holder.itemView.setOnClickListener(view -> listener.onItemClicked(position));
                break;
            case RECYCLER:
                AlertFieldModel m = items.get(position);
                ViewHolder2 h2 = (ViewHolder2)holder;
                if(m.strings == null || m.strings.size() == 0) {
                    h2.field.setVisibility(View.GONE);
                }
                else {
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.item_text, R.id.textview, m.strings);

                    h2.field.setAdapter(new SimpleAdapter(m.strings));
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
                    h2.field.setLayoutManager(mLayoutManager);
                    h2.field.setNestedScrollingEnabled(false);

                    h2.textView.setText(R.string.add_another_area);
                }
                h2.image.setImageDrawable(
                        context.getResources().getDrawable(items.get(position).drawable)
                );
                h2.textView.setOnClickListener(view -> listener.onItemClicked(position));
                break;
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
       return items.get(position).viewType;
    }
}

class AlertFieldModel {

    public int viewType;
    @DrawableRes public int drawable;
    @StringRes public int title;
    public List<String> strings;

    public AlertFieldModel(int viewType, int drawable, int title) {
        this.viewType = viewType;

        this.drawable = drawable;
        this.title = title;
    }

    public AlertFieldModel(int viewType, int drawable, int title, List<String> strings) {
        this.viewType = viewType;
        this.drawable = drawable;
        this.title = title;
        this.strings = strings;
    }
}

interface ClickListener {

    void onItemClicked(int position);

}


package org.alertpreparedness.platform.alert.dashboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.adapter.HazardAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HazardSelectionActivity extends AppCompatActivity implements HazardAdapter.HazardSelectionListner {

    public static final String HAZARD_TYPE = "hazard_type";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.rvHazardList)
    RecyclerView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hazard_selection);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mList.setAdapter(new HazardAdapter(this));
        mList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    @Override
    public void onHazardSelected(String hazardTitle) {
        Intent data = new Intent();
        data.putExtra(HAZARD_TYPE, hazardTitle);
        setResult(RESULT_OK, data);
        finish();
    }
}

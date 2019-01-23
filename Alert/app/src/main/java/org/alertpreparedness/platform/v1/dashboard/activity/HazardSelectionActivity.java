package org.alertpreparedness.platform.v1.dashboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import org.alertpreparedness.platform.v1.R;
import org.alertpreparedness.platform.v1.dashboard.adapter.HazardAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HazardSelectionActivity extends AppCompatActivity implements HazardAdapter.HazardSelectionListner {

    public static final String HAZARD_TYPE = "hazard_type";
    public static final String HAZARD_TITLE = "hazard_title";

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
    public void onHazardSelected(int position, String hazardTitle) {
        Intent data = new Intent();
        data.putExtra(HAZARD_TYPE, position);
        data.putExtra(HAZARD_TITLE, hazardTitle);
        setResult(RESULT_OK, data);
        finish();
    }
}

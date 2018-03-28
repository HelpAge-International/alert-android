package org.alertpreparedness.platform.alert.dashboard.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.adapter.HazardAdapter;
import org.alertpreparedness.platform.alert.dashboard.adapter.MultiHazardAdapter;
import org.alertpreparedness.platform.alert.utils.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MultiHazardSelectionActivity extends AppCompatActivity implements MultiHazardAdapter.MultiHazardSelectionListener {

    public static final String HAZARDS = "hazard_type";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.rvHazardList)
    RecyclerView mList;
    private MultiHazardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hazard_selection);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<Integer> hazardIds = getIntent().getIntegerArrayListExtra(HAZARDS);

        adapter = new MultiHazardAdapter(this);
        if(hazardIds != null){
            adapter.setSelected(Lists.newArrayList(Collections2.transform(hazardIds, Constants.Hazard::getById)));
        }

        mList.setAdapter(adapter);
        mList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    @Override
    public void onHazardSelected(List<Constants.Hazard> selectedHazards) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_multi_hazard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.btnDone){

            Intent intent = new Intent();
            intent.putIntegerArrayListExtra(HAZARDS, Lists.newArrayList(Collections2.transform(adapter.getSelected(), Enum::ordinal)));

            setResult(RESULT_OK, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

package org.alertpreparedness.platform.v1.dashboard.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import org.alertpreparedness.platform.v1.R;
import org.alertpreparedness.platform.v1.dashboard.adapter.MultiHazardAdapter;
import org.alertpreparedness.platform.v1.utils.Constants;

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

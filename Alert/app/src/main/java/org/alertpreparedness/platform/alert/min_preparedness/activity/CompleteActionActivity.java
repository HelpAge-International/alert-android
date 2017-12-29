package org.alertpreparedness.platform.alert.min_preparedness.activity;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertAdapter;
import org.alertpreparedness.platform.alert.dashboard.model.Tasks;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.AttachmentAdapter;
import org.alertpreparedness.platform.alert.utils.SimpleAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CompleteActionActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    RecyclerView rvComAttachments;
//    @Nullable
//    @BindView(R.id.rvCompletionAttachments)
//    RecyclerView rvComAttachments;

    public AttachmentAdapter attachmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_action);

        ButterKnife.bind(this);
        DependencyInjector.applicationComponent().inject(this);

        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvComAttachments = (RecyclerView) findViewById(R.id.rvCompletionAttachments);

        initView();

    }

   private void initView() {
//        rvComAttachments.setHasFixedSize(true);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//        rvComAttachments.setLayoutManager(layoutManager);
//        rvComAttachments.setNestedScrollingEnabled(false);
//        rvComAttachments.setAdapter(attachmentAdapter);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_confirm, menu);
        return true;
    }

// TODO   @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }
}

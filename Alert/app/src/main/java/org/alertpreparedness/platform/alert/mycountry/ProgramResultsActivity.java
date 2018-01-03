package org.alertpreparedness.platform.alert.mycountry;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelIndicatorLocation;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/**
 * Created by Tj on 19/12/2017.
 */

public class ProgramResultsActivity extends AppCompatActivity implements SupportAnimator.AnimatorListener, FilterAdapter.SelectListener, NestedScrollView.OnScrollChangeListener {

    public static final String BUNDLE_FILTER = "filter";
    public static final String TITLE_1 = "title_1";
    public static final String TITLE_2 = "title_2";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tvResultsFor)
    TextView mResultsFor;

    @BindView(R.id.cvReveal)
    CardView mRevealView;

    @BindView(R.id.rvFilters)
    RecyclerView mFilterList;

    @BindView(R.id.allNetworks)
    AppCompatCheckBox mAllNetworks;

    @BindView(R.id.tvAllAgencies)
    TextView tvAllAgencies;

    @BindView(R.id.rvFields)
    RecyclerView mResultsList;

    @BindView(R.id.scroller)
    NestedScrollView scrollView;

    @BindView(R.id.toolbar_layout)
    CardView toolbarCon;

    @BindView(R.id.revealCon)
    CardView revealCon;

    private ModelIndicatorLocation filter;

    private String mTitle1;
    private String mTitle2;
    private boolean hidden = true;
    private FilterAdapter mFilterAdapter;
    private float mToolbarElevation;
    private boolean hidding = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programme_results);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        filter = getIntent().getParcelableExtra(BUNDLE_FILTER);
        mTitle1 = getIntent().getStringExtra(TITLE_1);
        mTitle2 = getIntent().getStringExtra(TITLE_2);

        initViews();
    }

    private void initViews() {

        mResultsFor.setText(
                String.format(getString(R.string.results_for),
                        mTitle1,
                        (mTitle2 != null ? "," : ""),
                        (mTitle2 != null ? mTitle2 : "")
                )
        );

        int cx = (mRevealView.getLeft() + mRevealView.getRight());
        int cy = mRevealView.getTop();

        mRevealView.post(() -> {
            SupportAnimator animator =
                    ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, mRevealView.getHeight());
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(0);

            SupportAnimator animator_reverse = animator.reverse();
            animator_reverse.addListener(this);
            animator_reverse.start();
        });

        List<String> filters = new ArrayList<>();
        filters.add("START network");
        filters.add("START network");
        mFilterAdapter = new FilterAdapter(this, filters, this);
        mFilterList.setAdapter(mFilterAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.filter_divider));
        mFilterList.addItemDecoration(dividerItemDecoration);
        mFilterList.setLayoutManager(new LinearLayoutManager(this));

        List<ProgrammeInfo> programmes = new ArrayList<>();
        List<Programme> l = new ArrayList<Programme>();
        l.add(new Programme());
        programmes.add(new ProgrammeInfo("first", l));
        programmes.add(new ProgrammeInfo("first", l));
        programmes.add(new ProgrammeInfo("first", l));

        mResultsList.setLayoutManager(new LinearLayoutManager(this));
        mResultsList.setAdapter(new ProgrammesAdapter(this, programmes));

        scrollView.setNestedScrollingEnabled(false);
        ViewCompat.setNestedScrollingEnabled(scrollView, false);
        scrollView.setOnScrollChangeListener(this);

        revealCon.setCardElevation(0);

        mToolbarElevation = toolbarCon.getCardElevation();

    }

    @OnCheckedChanged(R.id.allNetworks)
    void allNetworksChecked(CompoundButton b, boolean checked) {
        if(checked) {
            mFilterAdapter.disableAll();
        }
        else {
            mFilterAdapter.enableAll();
        }
    }

    @OnCheckedChanged(R.id.allAgencies)
    void allAgenciesChecked(CompoundButton b, boolean checked) {
        if(checked) {
            mAllNetworks.setEnabled(false);
            mAllNetworks.setChecked(true);
            mAllNetworks.setAlpha(0.5f);
            mFilterAdapter.disableAll();
            tvAllAgencies.setAlpha(0.5f);
        }
        else {
            mAllNetworks.setEnabled(true);
            mAllNetworks.setAlpha(1f);
            mFilterAdapter.enableAll();
            tvAllAgencies.setAlpha(1f);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.programme_results_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_filter:

                int cx = (mRevealView.getLeft() + mRevealView.getRight());
                int cy = mRevealView.getTop();
                int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());
                SupportAnimator animator =
                        ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(300);

                SupportAnimator animator_reverse = animator.reverse();

                if (hidden) {
                    hidding = false;
                    mRevealView.setVisibility(View.VISIBLE);
                    animator.addListener(this);
                    animator.start();
                }
                else {
                    hidding = true;
                    animator_reverse.addListener(this);
                    animator_reverse.start();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAnimationStart() {
        if(!hidden) {
            revealCon.setCardElevation(0);
        }
    }

    @Override
    public void onAnimationEnd() {
        if(hidding) {
            mRevealView.setVisibility(View.INVISIBLE);
            hidden = true;
        }
        else {
            hidden = false;
        }
        if(!hidden) {
            revealCon.setCardElevation(mToolbarElevation);
        }
    }

    @Override
    public void onAnimationCancel() {

    }

    @Override
    public void onAnimationRepeat() {

    }

    @Override
    public void onItemSelected(int position) {

    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

    }
}

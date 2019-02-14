package org.alertpreparedness.platform.v1.mycountry;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.alertpreparedness.platform.v1.ExtensionHelperKt;
import org.alertpreparedness.platform.v1.R;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.AgencyBaseRef;
import org.alertpreparedness.platform.v1.dagger.annotation.NetworkRef;
import org.alertpreparedness.platform.v1.dagger.annotation.ProgrammeRef;
import org.alertpreparedness.platform.v1.firebase.AgencyModel;
import org.alertpreparedness.platform.v1.firebase.ProgrammeModel;
import org.alertpreparedness.platform.v1.risk_monitoring.model.CountryJsonData;
import org.alertpreparedness.platform.v1.risk_monitoring.model.ModelIndicatorLocation;
import org.alertpreparedness.platform.v1.risk_monitoring.view_model.SelectAreaViewModel;
import org.alertpreparedness.platform.v1.utils.AppUtils;

/**
 * Created by Tj on 19/12/2017.
 */

public class ProgramResultsActivity extends AppCompatActivity implements SupportAnimator.AnimatorListener, FilterAdapter.SelectListener, NestedScrollView.OnScrollChangeListener, ValueEventListener {

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

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.noResults)
    TextView noResults;

    @Inject
    @ProgrammeRef
    DatabaseReference programmesRef;

    @Inject
    @AgencyBaseRef
    DatabaseReference agencyRef;

    @Inject
    @NetworkRef
    DatabaseReference networkRef;

    private ModelIndicatorLocation filter;

    private String mTitle1;
    private String mTitle2;
    private boolean hidden = true;
    private FilterAdapter mFilterAdapter;
    private float mToolbarElevation;
    private boolean hidding = true;
    private HashMap<String, ArrayList<ProgrammeModel>> programmes = new HashMap<>();
    private HashMap<String, Boolean> agencyRequests = new HashMap<>();
    private ProgrammesAdapter mProgrammesAdapter;
    private ArrayList<ProgrammeInfo> mProgrammes;
    private HashMap<String, AgencyModel> agencyList = new HashMap<>();
    private ArrayList<CountryJsonData> mCountryDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programme_results_v1);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DependencyInjector.userScopeComponent().inject(this);

        filter = getIntent().getParcelableExtra(BUNDLE_FILTER);
        mTitle1 = getIntent().getStringExtra(TITLE_1);
        mTitle2 = getIntent().getStringExtra(TITLE_2);

        SelectAreaViewModel mViewModel = ViewModelProviders.of(this).get(SelectAreaViewModel.class);

        mViewModel.getCountryJsonDataLive().observe(this, countryJsonData -> {

            if(countryJsonData != null) {
                mCountryDataList = new ArrayList<>(countryJsonData);

                if (mCountryDataList.size() == 248) {
                    progressBar.setVisibility(View.GONE);
                    noResults.setVisibility(View.VISIBLE);
                    programmesRef.addValueEventListener(this);
                }
            }

        });

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

        mFilterAdapter = new FilterAdapter(this, this);
        mFilterList.setAdapter(mFilterAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.filter_divider));
        mFilterList.addItemDecoration(dividerItemDecoration);
        mFilterList.setLayoutManager(new LinearLayoutManager(this));

        mProgrammes = new ArrayList<>();
        mResultsList.setLayoutManager(new LinearLayoutManager(this));

        scrollView.setNestedScrollingEnabled(false);
        ViewCompat.setNestedScrollingEnabled(scrollView, false);
        scrollView.setOnScrollChangeListener(this);

        revealCon.setCardElevation(0);

        mToolbarElevation = toolbarCon.getCardElevation();

    }

    private void createAdapter() {
        System.out.println("mProgrammes = " + mProgrammes);
        mProgrammesAdapter = new ProgrammesAdapter(this, mProgrammes);
        mResultsList.setAdapter(mProgrammesAdapter);
    }

    @OnCheckedChanged(R.id.allNetworks)
    void allNetworksChecked(CompoundButton b, boolean checked) {
        if(checked) {
            mFilterAdapter.disableAll();
            mFilterAdapter.checkAll();
            showNetworkProgrammesOnly();
        }
        else {
            mFilterAdapter.enableAll();
            mFilterAdapter.checkAll();
        }
    }

    @OnCheckedChanged(R.id.allAgencies)
    void allAgenciesChecked(CompoundButton b, boolean checked) {
        if(checked) {
            mAllNetworks.setEnabled(false);
            mAllNetworks.setChecked(true);
            mAllNetworks.setAlpha(0.5f);
            mFilterAdapter.disableAll();
            mFilterAdapter.checkAll();
            tvAllAgencies.setAlpha(0.5f);
        }
        else {
            mAllNetworks.setEnabled(true);
            mAllNetworks.setAlpha(1f);
            mFilterAdapter.disableAll();
            mFilterAdapter.checkAll();
            tvAllAgencies.setAlpha(1f);
        }
    }

    private void showNetworkProgrammesOnly() {
        ArrayList<ProgrammeInfo> list = new ArrayList<>();
        List<String> keys = new ArrayList<>();

        for(Map.Entry<String, FilterAdapter.NetworkHolder> holder : mFilterAdapter.getItems().entrySet()) {
            for (String agencyId : holder.getValue().getIds()) {
                if(keys.indexOf(agencyId) == -1) {
                    keys.add(agencyId);
                    list.add(new ProgrammeInfo(agencyList.get(agencyId), programmes.get(agencyId)));
                }
            }

        }

        mProgrammesAdapter = new ProgrammesAdapter(this, list);
        mResultsList.setAdapter(mProgrammesAdapter);
        mResultsList.setLayoutManager(new LinearLayoutManager(this));
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

        ArrayList<ProgrammeInfo> list = new ArrayList<>();
        List<String> keys = new ArrayList<>();

        for(Integer pos : mFilterAdapter.getCheckedItems()) {
            FilterAdapter.NetworkHolder holder = mFilterAdapter.getModel(pos);

            for (String agencyId : holder.getIds()) {
                if (keys.indexOf(agencyId) == -1) {
                    keys.add(agencyId);

                    list.add(new ProgrammeInfo(agencyList.get(agencyId), programmes.get(agencyId)));

                }
            }
        }

        mProgrammesAdapter = new ProgrammesAdapter(this, list);
        mResultsList.setAdapter(mProgrammesAdapter);
        mResultsList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            for(DataSnapshot programmSnapshot : child.child("4WMapping").getChildren()) {
                ProgrammeModel model = AppUtils.getValueFromDataSnapshot(programmSnapshot, ProgrammeModel.class);
                assert model != null;

                model.setKey(programmSnapshot.getKey());
                model.setCountryName(mTitle1);
                List<String> level1Values = ExtensionHelperKt.getLevel1Values(filter.getCountry(),
                        mCountryDataList
                );
                if(level1Values != null && level1Values.size() > 0) {
                    model.setLevel1Name(level1Values.get(model.getLevel1()));

                    List<String> level2Values = ExtensionHelperKt.getLevel2Values(filter.getCountry(), model.getLevel1(), mCountryDataList);

                    if (model.getLevel2() != null && level2Values != null && level2Values.size() > Integer.parseInt(model.getLevel2())) {
                        model.setLevel2Name(level2Values.get(Integer.parseInt(model.getLevel2())));
                    }
                }

                boolean hasLevel1 = filter.getLevel1() != null && filter.getLevel1() != -1;
                boolean hasLevel2 = filter.getLevel2() != null && filter.getLevel2() != -1;

                if(filter.getLevel1() == -1) {
                    if(model.getAgencyId() != null) {
                        ArrayList<ProgrammeModel> models = programmes.get(model.getAgencyId());
                        if (models == null) {
                            models = new ArrayList<>();
                        }
                        models.add(model);
                        programmes.put(model.getAgencyId(), models);
                        agencyRequests.put(model.getAgencyId(), false);
                    }
                }
                else if((!hasLevel1 || filter.getLevel1() == model.getLevel1()) && (!hasLevel2 || filter.getLevel2().toString().equals(model.getLevel2()))) {
                    if(model.getAgencyId() != null) {
                        ArrayList<ProgrammeModel> models = programmes.get(model.getAgencyId());

                        if (programmes.get(model.getAgencyId()) == null) {
                            models = new ArrayList<>();
                        }
                        models.add(model);
                        programmes.put(model.getAgencyId(), models);
                        agencyRequests.put(model.getAgencyId(), false);
                    }
                }
            }
        }

        if(agencyRequests.size() > 0) {
            for (String id : agencyRequests.keySet()) {
                if(id != null) {
                    System.out.println(agencyRequests.keySet());
                    agencyRef.child(id).addListenerForSingleValueEvent(new AgencyListener());
                }
            }
        }

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private class AgencyListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            AgencyModel model = dataSnapshot.getValue(AgencyModel.class);

            assert model != null;
            HashMap<String, Boolean> networks = null;
            try {
                networks = (HashMap<String, Boolean>)dataSnapshot.child("networks").getValue();
            }
            catch (Exception e) {

            }

            mProgrammes.add(new ProgrammeInfo(model, programmes.get(dataSnapshot.getKey())));
            agencyRequests.put(dataSnapshot.getKey(), true);
            agencyList.put(dataSnapshot.getKey(), model);

            if(networks != null) {
                for (String id : networks.keySet()) {
                    DatabaseReference ref = networkRef.child(id);
                    ref.addValueEventListener(new NetworkListener(dataSnapshot.getKey()));
                }
            }

            if(allAgencyRequestsComplete()) {
                System.out.println("agencyComplete");
                createAdapter();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private class NetworkListener implements ValueEventListener {

        private String key;

        public NetworkListener(String key) {
            this.key = key;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            String name = dataSnapshot.child("name").getValue(String.class);

            mFilterAdapter.addItem(name, dataSnapshot.getKey(),key);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private boolean allAgencyRequestsComplete() {
        boolean res = true;

        for(Map.Entry<String, Boolean> entry : agencyRequests.entrySet()) {
            Boolean value = entry.getValue();
            res = res && value;
        }
        return res;
    }
}

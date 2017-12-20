package org.alertpreparedness.platform.alert.mycountry;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelIndicatorLocation;
import org.alertpreparedness.platform.alert.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tj on 19/12/2017.
 */

public class ProgramResultsActivity extends AppCompatActivity {

    public static final String BUNDLE_FILTER = "filter";
    public static final String TITLE_1 = "title_1";
    public static final String TITLE_2 = "title_2";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tvResultsFor)
    TextView mResultsFor;

    private ModelIndicatorLocation filter;
    private String mTitle1;
    private String mTitle2;

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

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}

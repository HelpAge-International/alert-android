package org.alertpreparedness.platform.v1.dashboard.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import io.reactivex.disposables.CompositeDisposable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import org.alertpreparedness.platform.BuildConfig;
import org.alertpreparedness.platform.v1.ExtensionHelperKt;
import org.alertpreparedness.platform.R;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseAlertRef;
import org.alertpreparedness.platform.v1.dashboard.adapter.AlertAdapter;
import org.alertpreparedness.platform.v1.firebase.AffectedAreaModel;
import org.alertpreparedness.platform.v1.firebase.AlertModel;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.risk_monitoring.model.CountryJsonData;
import org.alertpreparedness.platform.v1.risk_monitoring.view_model.SelectAreaViewModel;
import org.alertpreparedness.platform.v1.utils.Constants;
import org.alertpreparedness.platform.v1.utils.PreferHelper;

public class AlertDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtHazardName, txtPopulation, txtAffectedArea, txtInfo, txtLastUpdated, txtActionBarTitle,
            txtRedRequested;

    private ImageView imgHazard, imgPopulation, imgAffectedArea, imgInfo, imgClose, imgUpdate;

    private Toolbar toolbar;

    private Calendar date = Calendar.getInstance();

    private String dateFormat = "dd/MM/yyyy";

    private SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());

    private AlertModel alert;

    private String isRequestSent;

    private LinearLayout llButtons;

    private String countryID;

    private boolean isCountryDirector;

    private String mAppStatus;

    private Button btnApprove, btnReject;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static final String EXTRA_ALERT = "extra_alert";

    @Inject
    SimpleDateFormat dateFormatter;

    @Inject
    @BaseAlertRef
    DatabaseReference baseAlertRef;

    @BindView(R.id.redAlertReasonTxt)
    TextView redAlertReasonText;

    @BindView(R.id.redAlertReasonIcon)
    ImageView redAlertReasonIcon;

    @BindView(R.id.redAlertReason)
    TextView redAlertTextView;

    @Inject
    User user;

    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();

    ValueEventListener mValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            parseAlert();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ArrayList<CountryJsonData> mCountryDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_detail_v1);

        ButterKnife.bind(this);
        DependencyInjector.userScopeComponent().inject(this);

        countryID = user.countryID;
        isCountryDirector = user.isCountryDirector();
        toolbar = findViewById(R.id.action_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Change the color of the arrow
        final Drawable upArrow = toolbar.getNavigationIcon();
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        initView();

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void parseAlert() {

        if (alert.getAlertLevel() != null) {
            long alertLevel = alert.getAlertLevel();

            if (alertLevel != 0) {
                long hazardScenario = alert.getHazardScenario();

                if (alert.getTimeUpdated() != null) {
                    if (hazardScenario != -1) {
                        fetchDetails();
                    } else if (alert.getOtherName() != null) {
                        String nameId = alert.getOtherName();

                        setOtherName(nameId);
                    }

                } else if (alert.getTimeCreated() != null) {
                    if (hazardScenario != -1) {
                        fetchDetails();
                    } else if (alert.getOtherName() != null) {
                        String nameId = alert.getOtherName();
                        setOtherName(nameId);
                    }
                }
            }
        }
    }

    private void setOtherName(String nameId) {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference().child(mAppStatus).child("hazardOther").child(nameId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = (String) dataSnapshot.child("name").getValue();
                AlertDetailActivity.this.alert.setOtherName(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initView() {

        llButtons = findViewById(R.id.llButtons);
        txtHazardName = findViewById(R.id.txtHazardName);
        txtPopulation = findViewById(R.id.txtPopulationAffected);
        txtAffectedArea = findViewById(R.id.txtArea);
        txtInfo = findViewById(R.id.txtInfo);
        txtLastUpdated = findViewById(R.id.txtLastUpdated);
        txtActionBarTitle = findViewById(R.id.action_bar_title);
        txtRedRequested = findViewById(R.id.tvRedRequested);
        imgHazard = findViewById(R.id.imgHazardIcon);
        imgPopulation = findViewById(R.id.imgPopulationIcon);
        imgAffectedArea = findViewById(R.id.imgAreaIcon);
        imgInfo = findViewById(R.id.imgInfo);
        imgClose = findViewById(R.id.leftImageView);
        imgUpdate = findViewById(R.id.rightImageView);
        btnApprove = findViewById(R.id.btnApprove);
        btnReject = findViewById(R.id.btnReject);

        imgUpdate.setImageResource(R.drawable.ic_create_white_24dp);
        btnApprove.setOnClickListener(this);
        btnReject.setOnClickListener(this);
        imgClose.setVisibility(View.GONE);
        txtRedRequested.setVisibility(View.GONE);
        llButtons.setVisibility(View.GONE);

        if (alert == null) {
            Intent intent = getIntent();
            Bundle bd = intent.getExtras();
            if (bd != null) {
                isRequestSent = (String) bd.get("IS_RED_REQUEST");
            }
            alert = (AlertModel) intent.getSerializableExtra(EXTRA_ALERT);
            fetchDetails();
            mAppStatus = BuildConfig.ROOT_NODE;

            mReference =
                    FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child(mAppStatus)
                            .child("alert")
                            .child(alert.getParentKey())
                            .child(alert.getId());
            mReference.addValueEventListener(mValueListener);
        }

        if (alert.getAlertLevel() != Constants.TRIGGER_RED) {
            redAlertReasonIcon.setVisibility(View.GONE);
            redAlertReasonText.setVisibility(View.GONE);
            redAlertTextView.setVisibility(View.GONE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imgAffectedArea.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.txtInfo);

            imgAffectedArea.setLayoutParams(params);
        } else {
            redAlertReasonText.setText(alert.getReasonForRedAlert());
        }
    }

    private void approveOrReject(boolean isApproved) {
        if (isApproved) {
            mReference.child("approval").child("countryDirector").child(countryID).setValue(Constants.REQ_APPROVED);
            mReference.child("redAlertApproved").setValue(true);
            mReference.child("level").setValue(Constants.TRIGGER_RED);
            if (alert.getPreviousAmber()) {
                alert.getTimeTracking()
                        .updateAlertTimeTracking(Constants.TRIGGER_AMBER, Constants.TRIGGER_RED);

            } else {
                alert.getTimeTracking()
                        .updateAlertTimeTracking(Constants.TRIGGER_GREEN, Constants.TRIGGER_RED);
            }
        } else {
            mReference.child("redAlertApproved").setValue(false);
            if (alert.getPreviousAmber()) {
                mReference.child("level").setValue(Constants.TRIGGER_AMBER);
            } else {
                mReference.child("level").setValue(Constants.TRIGGER_GREEN);
            }
            mReference.child("approval").child("countryDirector").child(countryID).setValue(Constants.REQ_REJECTED);
        }

        finish();
    }

    private void fetchDetails() {
        setUpActionBarColour();
        setUpRedAlertRequestView();

        SelectAreaViewModel mViewModel = ViewModelProviders.of(this).get(SelectAreaViewModel.class);

        mViewModel.getCountryJsonDataLive().observe(this, countryJsonData -> {

            if (countryJsonData != null) {
                mCountryDataList = new ArrayList<>(countryJsonData);

                if (mCountryDataList.size() >= 248) {

                    StringBuilder res = new StringBuilder();
                    for (AffectedAreaModel m : alert.getAffectedAreas()) {
                        if (m != null) {
                            res.append(Constants.COUNTRIES[m.getCountry()]);
                            try {
                                List<String> list = ExtensionHelperKt
                                        .getLevel1Values(m.getCountry(), mCountryDataList);
                                List<String> level2List = ExtensionHelperKt
                                        .getLevel2Values(m.getCountry(), m.getLevel1(), mCountryDataList);

                                if (list != null) {
                                    if (m.getLevel1() != null && m.getLevel1() != -1
                                            && list.get(m.getLevel1()) != null) {
                                        m.setLevel1Name(list.get(m.getLevel1()));
                                        res.append(", ").append(list.get(m.getLevel1()));
                                    }
                                    if (m.getLevel2() != null && m.getLevel2() != -1 && level2List != null
                                            && level2List.get(m.getLevel2()) != null) {
                                        m.setLevel2Name(level2List.get(m.getLevel2()));
                                        res.append(", ").append(m.getLevel2Name());
                                    }
                                }
                            } catch (Exception e) {
                                //                            e.printStackTrace();
                            }
                            res.append("\n");

                        }
                    }

                    txtAffectedArea.setText(res.toString());
                    imgUpdate.setOnClickListener(this);

                }
            }

        });

        imgPopulation.setImageResource(R.drawable.alert_population);
        imgAffectedArea.setImageResource(R.drawable.alert_areas);
        imgInfo.setImageResource(R.drawable.alert_information);

        for (int i = 0; i < Constants.HAZARD_SCENARIO_NAME.length; i++) {
            if (i == alert.getHazardScenario()) {
                AlertAdapter.fetchIcon(Constants.HAZARD_SCENARIO_NAME[i], imgHazard);
                txtHazardName.setText(Constants.HAZARD_SCENARIO_NAME[i]);
                txtPopulation.setText(getPeopleAsString(alert.getEstimatedPopulation()));
                if (alert.getTimeUpdated() != null) {
                    txtLastUpdated.setText(getUpdatedAsString(new Date(alert.getTimeUpdated())));
                }
                txtInfo.setText(alert.getInfoNotes());
            } else if (alert.getOtherName() != null) {
                Long date = alert.getTimeUpdated();
                if (date == null) {
                    date = alert.getTimeCreated();
                }
                imgHazard.setImageResource(R.drawable.other);
                txtHazardName.setText(alert.getOtherName());
                txtPopulation.setText(getPeopleAsString(alert.getEstimatedPopulation()));
                txtInfo.setText(alert.getInfoNotes());
                txtLastUpdated.setText(getUpdatedAsString(new Date(date)));
            }
        }
    }

    private String getRedDisplayText(String fn, String ln) {

        Long date = alert.getTimeUpdated() == null ? alert.getTimeCreated() : alert.getTimeUpdated();

        return fn + " " + ln + " has requested the alert level to go from Amber to Red on the " + dateFormatter
                .format(date);
    }


    private String getPeopleAsString(long population) {
        return population + " people";
    }

    private SpannableStringBuilder getUpdatedAsString(Date date) {
        String updateDate = dateFormatter.format(date);
        SpannableStringBuilder sb = new SpannableStringBuilder("Last updated: " + updateDate);
        StyleSpan b = new StyleSpan(android.graphics.Typeface.BOLD);
        sb.setSpan(b, 14, 14 + updateDate.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return sb;
    }

    private void setUpActionBarColour() {
        Window window = getWindow();
        if (alert.getAlertLevel() == 1) {
            toolbar.setBackgroundResource(R.color.alertAmber);
            txtActionBarTitle.setText(R.string.amber_alert_text);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.sBar_Amber));
            }

        } else if (alert.getAlertLevel() == 2) {
            toolbar.setBackgroundResource(R.color.alertRed);
            txtActionBarTitle.setText(R.string.red_alert_text);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.sBar_Red));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReference != null && mValueListener != null) {
            mReference.removeEventListener(mValueListener);
        }
        compositeDisposable.clear();
    }

    @Override
    public void onClick(View view) {

        if (view == imgUpdate) {
            Intent intent = new Intent(AlertDetailActivity.this, UpdateAlertActivity.class);
            intent.putExtra(EXTRA_ALERT, alert);
            startActivity(intent);
            finish();
        }

        if (view == btnApprove) {
            approveOrReject(true);
        }

        if (view == btnReject) {
            approveOrReject(false);
        }

    }

    private void setUpRedAlertRequestView() {
        Window window = getWindow();
        if (alert.isNetwork() && alert.getAgencyAdminId().equals(alert.getLeadAgencyId()) && alert.getAgencyAdminId()
                .equals(user.getUserID()) && !alert.getRedAlertApproved()
                && alert.getAlertLevel() == Constants.TRIGGER_RED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.sBar_Gray));
            }
            toolbar.setBackgroundResource(R.color.alertGrey);
            txtActionBarTitle.setText(R.string.amber_alert_text);
            txtRedRequested.setVisibility(View.VISIBLE);
            llButtons.setVisibility(View.VISIBLE);
            setUserName();
        } else if (!alert.isNetwork() && isCountryDirector && !alert.getRedAlertApproved()
                && alert.getAlertLevel() == Constants.TRIGGER_RED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.sBar_Gray));
            }
            toolbar.setBackgroundResource(R.color.alertGrey);
            txtActionBarTitle.setText(R.string.amber_alert_text);
            txtRedRequested.setVisibility(View.VISIBLE);
            llButtons.setVisibility(View.VISIBLE);
            setUserName();
        } else if (!isCountryDirector && !alert.getRedAlertApproved()
                && alert.getAlertLevel() == Constants.TRIGGER_RED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.sBar_Gray));
            }
            toolbar.setBackgroundResource(R.color.alertGrey);
            txtActionBarTitle.setText(R.string.amber_alert_text);
            txtRedRequested.setVisibility(View.VISIBLE);
        }
    }

    private void setUserName() {

        String userId = alert.getUpdatedBy();

        if (userId == null) {
            userId = alert.getCreatedBy();
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference db = ref.
                child(BuildConfig.ROOT_NODE).
                child("userPublic").child(userId);

        System.out.println("REF: " + db.getRef());
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String firstname = dataSnapshot.child("firstName").getValue().toString();
                String lastname = dataSnapshot.child("lastName").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();

                txtRedRequested.setText(getRedDisplayText(firstname, lastname));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        fetchDetails();
        super.onStart();
    }

}

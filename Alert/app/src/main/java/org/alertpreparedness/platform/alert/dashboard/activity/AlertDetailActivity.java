package org.alertpreparedness.platform.alert.dashboard.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.alertpreparedness.platform.alert.ExtensionHelperKt;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertAdapter;
import org.alertpreparedness.platform.alert.firebase.AffectedAreaModel;
import org.alertpreparedness.platform.alert.firebase.AlertModel;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.dashboard.model.Alert;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.risk_monitoring.model.CountryJsonData;
import org.alertpreparedness.platform.alert.risk_monitoring.service.RiskMonitoringService;
import org.alertpreparedness.platform.alert.risk_monitoring.view_model.SelectAreaViewModel;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.PreferHelper;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AlertDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtHazardName, txtPopulation, txtAffectedArea, txtInfo, txtLastUpdated, txtActionBarTitle, txtRedRequested;
    private ImageView imgHazard, imgPopulation, imgAffectedArea, imgInfo, imgClose, imgUpdate;
    private Toolbar toolbar;
    private Calendar date = Calendar.getInstance();
    private String dateFormat = "dd/MM/yyyy";
    private SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());
    private AlertModel alert;
    private String isRequestSent;
    private ConstraintLayout clRequested;
    private LinearLayout llButtons;
    private String countryID;
    private boolean isCountryDirector;
    private String mAppStatus;
    private Button btnApprove, btnReject;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private UpdateAlertActivity updateAlertActivity = new UpdateAlertActivity();

    public static final String EXTRA_ALERT = "extra_alert";

    @Inject
    SimpleDateFormat dateFormatter;

    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
    ValueEventListener mValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            System.out.println("dataSnapshot = " + dataSnapshot);



            parseAlert(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    private ArrayList<CountryJsonData> mCountryDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_detail);

        DependencyInjector.applicationComponent().inject(this);

//        countryID = UserInfo.getUser(this).countryID;
        isCountryDirector = UserInfo.getUser(this).isCountryDirector();
        isCountryDirector = true;

        if (UserInfo.getUser(this).isCountryDirector()) {
            System.out.println("CD or Not: " + UserInfo.getUser(this).isCountryDirector());
        }

        toolbar = (Toolbar) findViewById(R.id.action_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Change the color of the arrow
        final Drawable upArrow = toolbar.getNavigationIcon();
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);



        initView();

    }

    private void initView() {

        clRequested = findViewById(R.id.clRedRequested);
        llButtons = findViewById(R.id.llButtons);
        txtHazardName = (TextView) findViewById(R.id.txtHazardName);
        txtPopulation = (TextView) findViewById(R.id.txtPopulationAffected);
        txtAffectedArea = (TextView) findViewById(R.id.txtArea);
        txtInfo = (TextView) findViewById(R.id.txtInfo);
        txtLastUpdated = (TextView) findViewById(R.id.txtLastUpdated);
        txtActionBarTitle = (TextView) findViewById(R.id.action_bar_title);
        txtRedRequested = (TextView) findViewById(R.id.tvRedRequested);
        imgHazard = (ImageView) findViewById(R.id.imgHazardIcon);
        imgPopulation = (ImageView) findViewById(R.id.imgPopulationIcon);
        imgAffectedArea = (ImageView) findViewById(R.id.imgAreaIcon);
        imgInfo = (ImageView) findViewById(R.id.imgInfo);
        imgClose = (ImageView) findViewById(R.id.leftImageView);
        imgUpdate = (ImageView) findViewById(R.id.rightImageView);
        btnApprove = (Button) findViewById(R.id.btnApprove);
        btnReject = (Button) findViewById(R.id.btnReject);

        imgUpdate.setImageResource(R.drawable.ic_create_white_24dp);
        btnApprove.setOnClickListener(this);
        btnReject.setOnClickListener(this);
        imgClose.setVisibility(View.GONE);
        clRequested.setVisibility(View.GONE);
        llButtons.setVisibility(View.GONE);

        if (alert == null) {
            Intent intent = getIntent();
            Bundle bd = intent.getExtras();
            if (bd != null) {
                isRequestSent = (String) bd.get("IS_RED_REQUEST");
                System.out.println("REQ: " + isRequestSent);
            }
            alert = (AlertModel) intent.getSerializableExtra(EXTRA_ALERT);
            fetchDetails();

            mAppStatus = PreferHelper.getString(getApplicationContext(), Constants.APP_STATUS);

            mReference = FirebaseDatabase.getInstance().getReference().child(mAppStatus).child("alert").child(alert.getParentKey()).child(alert.getKey());
            mReference.addValueEventListener(mValueListener);
        }
    }

    private void parseAlert(DataSnapshot dataSnapshot) {
        if (dataSnapshot.child("alertLevel").getValue() != null) {
            long alertLevel = (long) dataSnapshot.child("alertLevel").getValue();
            String id = dataSnapshot.getKey();

            if (alertLevel != 0) {
                long hazardScenario = (long) dataSnapshot.child("hazardScenario").getValue();

                if (dataSnapshot.child("timeUpdated").exists()) {
                    if (hazardScenario != -1) {
                        fetchDetails();
                    } else if (dataSnapshot.child("otherName").exists()) {
                        String nameId = (String) dataSnapshot.child("otherName").getValue();

                        setOtherName(nameId);
                    }

                } else if (dataSnapshot.child("timeCreated").exists()) {
                    if (hazardScenario != -1) {
                        fetchDetails();
                    } else if (dataSnapshot.child("otherName").exists()) {
                        String nameId = (String) dataSnapshot.child("otherName").getValue();
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

    private void fetchDetails() {
        setUpActionBarColour();
        setUpRedAlertRequestView();

        SelectAreaViewModel mViewModel = ViewModelProviders.of(this).get(SelectAreaViewModel.class);

        mViewModel.getCountryJsonDataLive().observe(this, countryJsonData -> {

            if(countryJsonData != null) {
                mCountryDataList = new ArrayList<>(countryJsonData);

                if (mCountryDataList.size() > 240) {

                    StringBuilder res = new StringBuilder();
                    for(AffectedAreaModel m : alert.getAffectedAreas()) {
                        List<String> list = ExtensionHelperKt.getLevel1Values(m.getCountry(), mCountryDataList);
                        res.append(Constants.COUNTRIES[m.getCountry()]);
                        if(list != null && m.getLevel1() != null && list.get(m.getLevel1()) != null) {
                            m.setLevel1Name(list.get(m.getLevel1()));
                            res.append(", ").append(list.get(m.getLevel1()));
                        }
                        res.append("\n");
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
                if(alert.getTimeUpdated() != null) {
                    txtLastUpdated.setText(getUpdatedAsString(new Date(alert.getTimeUpdated())));
                }
                txtInfo.setText((CharSequence) alert.getInfoNotes());
            } else if (alert.getOtherName() != null) {
                imgHazard.setImageResource(R.drawable.other);
                txtHazardName.setText(alert.getOtherName());
                txtPopulation.setText(getPeopleAsString(alert.getEstimatedPopulation()));
                txtInfo.setText((CharSequence) alert.getInfoNotes());
                txtLastUpdated.setText(getUpdatedAsString(new Date(alert.getTimeUpdated())));
            }
        }
    }

    private void setUpRedAlertRequestView() {
        Window window = getWindow();
        if (isCountryDirector && alert.wasRedAlertRequested()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.sBar_Gray));
            }
            toolbar.setBackgroundResource(R.color.alertGray);
            txtActionBarTitle.setText(R.string.amber_alert_text);
            clRequested.setVisibility(View.VISIBLE);
            llButtons.setVisibility(View.VISIBLE);
            setUserName();
        } else if (!isCountryDirector && alert.wasRedAlertRequested()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.sBar_Gray));
            }
            toolbar.setBackgroundResource(R.color.alertGray);
            txtActionBarTitle.setText(R.string.amber_alert_text);
            clRequested.setVisibility(View.VISIBLE);
        }
    }

    private void setUserName() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference db = ref.
                child(PreferHelper.getString(getApplicationContext(), Constants.APP_STATUS)).
                child("userPublic").child(alert.getUpdatedBy());

        System.out.println("REF: " + db.getRef());
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String firstname = dataSnapshot.child("firstName").getValue().toString();
                String lastname = dataSnapshot.child("lastName").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();

                txtRedRequested.setText(getRedDisplayText(firstname, lastname));

                User user = new User(firstname, lastname, email);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private String getRedDisplayText(String fn, String ln) {
        return fn + " " + ln + " has requested the alert level to go from Amber to Red on the " + dateFormatter.format(new Date(alert.getTimeUpdated()));
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
            System.out.println("alertintent = " + alert);
            startActivity(intent);
        }

        if (view == btnApprove) {
            approveOrReject(true);
        }

        if (view == btnReject) {
            approveOrReject(false);
        }

    }

    private void approveOrReject(boolean isApproved) {

        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isApproved) {
                    mReference.child("approval").child("countryDirector").child(countryID).setValue(Constants.REQ_APPROVED);
                    mReference.child("alertLevel").setValue(Constants.TRIGGER_RED);
                } else {
                    mReference.child("approval").child("countryDirector").child(countryID).setValue(Constants.REQ_REJECTED);
                }
                Intent intent = new Intent(AlertDetailActivity.this, HomeScreen.class);
                startActivity(intent);
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

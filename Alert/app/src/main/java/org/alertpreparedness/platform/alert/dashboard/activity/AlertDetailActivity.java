package org.alertpreparedness.platform.alert.dashboard.activity;

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

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertAdapter;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.home.HomeScreen;
import org.alertpreparedness.platform.alert.model.Alert;
import org.alertpreparedness.platform.alert.risk_monitoring.service.RiskMonitoringService;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.PreferHelper;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
    private Alert alert;
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

    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
    ValueEventListener mValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            parseAlert(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_detail);

        countryID = UserInfo.getUser(this).countryID;
        isCountryDirector = UserInfo.getUser(this).isCountryDirector();

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

        clRequested = (ConstraintLayout) findViewById(R.id.clRedRequested);
        llButtons = (LinearLayout) findViewById(R.id.llButtons);
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
        imgUpdate.setOnClickListener(this);
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
            alert = (Alert) intent.getSerializableExtra(EXTRA_ALERT);
            fetchDetails();

            mAppStatus =  PreferHelper.getString(getApplicationContext(), Constants.APP_STATUS);

            mReference = FirebaseDatabase.getInstance().getReference().
                    child(mAppStatus).child("alert").child(countryID)
                    .child(alert.getId());
            mReference.addValueEventListener(mValueListener);
        }
    }

    private void parseAlert(DataSnapshot dataSnapshot) {
        if (dataSnapshot.child("alertLevel").getValue() != null) {
            long alertLevel = (long) dataSnapshot.child("alertLevel").getValue();
            String id = dataSnapshot.getKey();

            if (alertLevel != 0) {
                long numberOfAreas = dataSnapshot.child("affectedAreas").getChildrenCount();
                Log.e("f",id+" "+numberOfAreas);
                long country = (long) dataSnapshot.child("affectedAreas").getChildren().iterator().next().child("country").getValue();
                long hazardScenario = (long) dataSnapshot.child("hazardScenario").getValue();
                long population = (long) dataSnapshot.child("estimatedPopulation").getValue();
                long redStatus = (long) dataSnapshot.child("approval").child("countryDirector").child(countryID).getValue();
                String info = (String) dataSnapshot.child("infoNotes").getValue();

                if (dataSnapshot.child("timeUpdated").exists()) {
                    long updated = (long) dataSnapshot.child("timeUpdated").getValue();
                    date.setTimeInMillis(updated);
                    String updatedDay = format.format(date.getTime());

                    if (hazardScenario != -1) {
                        Alert alert = new Alert(alertLevel, hazardScenario, population,
                                numberOfAreas, redStatus, info, updatedDay, null);
                        alert.setId(id);

                        this.alert = alert;
                        fetchDetails();
                    } else if (dataSnapshot.child("otherName").exists()) {
                        String nameId = (String) dataSnapshot.child("otherName").getValue();
                        long level1 = alert.getLevel1();
                        long level2 = alert.getLevel2();
                        setOtherName(nameId, alertLevel, hazardScenario, numberOfAreas,
                                redStatus, population, country, level1, level2, info, updatedDay);
                    }

                } else if (dataSnapshot.child("timeCreated").exists()) {

                    String updatedDay = this.alert.getUpdated();

                    if (hazardScenario != -1) {
                        Alert alert = new Alert(alertLevel, hazardScenario, population, numberOfAreas,
                                redStatus, info, updatedDay, null);
                        alert.setId(id);

                        this.alert = alert;
                        fetchDetails();
                    } else if (dataSnapshot.child("otherName").exists()) {
                        String nameId = (String) dataSnapshot.child("otherName").getValue();
                        long level1 = alert.getLevel1();
                        long level2 = alert.getLevel2();

                        setOtherName(nameId, alertLevel, hazardScenario, numberOfAreas,
                                redStatus, population, country, level1, level2, info, updatedDay);
                    }

                }
            }
        }
    }
    private void setOtherName(String nameId, long alertLevel, long hazardScenario, long numOfAreas, long redStatus, long population, long country, long level1, long level2, String info, String updatedDay) {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference().child(mAppStatus).child("hazardOther").child(nameId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = (String) dataSnapshot.child("name").getValue();
                Alert alert = new Alert(alertLevel, hazardScenario, population, numOfAreas, redStatus, info, updatedDay, name);
                Alert alert1 = new Alert(country, level1, level2);
                alert.setId(dataSnapshot.getKey());

                AlertDetailActivity.this.alert = alert;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fetchDetails() {
        setUpActionBarColour();
        setUpRedAlertRequestView();

        for (int i = 0; i < Constants.COUNTRIES.length; i++) {
            if (alert.getCountry() == i) {
                txtAffectedArea.setText(Constants.COUNTRIES[i]);
            }
        }

        imgPopulation.setImageResource(R.drawable.alert_population);
        imgAffectedArea.setImageResource(R.drawable.alert_areas);
        imgInfo.setImageResource(R.drawable.alert_information);

        for (int i = 0; i < Constants.HAZARD_SCENARIO_NAME.length; i++) {
            if (i == alert.getHazardScenario()) {
                AlertAdapter.fetchIcon(Constants.HAZARD_SCENARIO_NAME[i], imgHazard);
                txtHazardName.setText(Constants.HAZARD_SCENARIO_NAME[i]);
                txtPopulation.setText(getPeopleAsString(alert.getPopulation()));
                txtLastUpdated.setText(getUpdatedAsString(alert.getUpdated()));
                txtInfo.setText((CharSequence) alert.getInfo());
            } else if (alert.getOtherName() != null) {
                imgHazard.setImageResource(R.drawable.other);
                txtHazardName.setText(alert.getOtherName());
                txtPopulation.setText(getPeopleAsString(alert.getPopulation()));
                txtInfo.setText((CharSequence) alert.getInfo());
                txtLastUpdated.setText(getUpdatedAsString(alert.getUpdated()));
            }
        }


    }

    private void setUpRedAlertRequestView() {
        Window window = getWindow();

        if (isCountryDirector && alert.getRedAlertRequested() == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.sBar_Gray));
            }
            toolbar.setBackgroundResource(R.color.alertGray);
            txtActionBarTitle.setText(R.string.amber_alert_text);
            txtRedRequested.setText(getRedDisplayText(alert.getUpdated()));
            clRequested.setVisibility(View.VISIBLE);
            llButtons.setVisibility(View.VISIBLE);

        }else if (!isCountryDirector && alert.getRedAlertRequested() == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.sBar_Gray));
            }
            toolbar.setBackgroundResource(R.color.alertGray);
            txtActionBarTitle.setText(R.string.amber_alert_text);
            clRequested.setVisibility(View.VISIBLE);
        }
    }

    private String getRedDisplayText(String updated) {
        return "A user has requested the alert level to go from Amber to Red on the "+updated;
    }

    private String getPeopleAsString(long population) {
        return population + " people";
    }

    private SpannableStringBuilder getUpdatedAsString(String updateDate) {
        SpannableStringBuilder sb = new SpannableStringBuilder("Last updated: " + updateDate);
        StyleSpan b = new StyleSpan(android.graphics.Typeface.BOLD);
        sb.setSpan(b, 14, 14 + updateDate.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return sb;
    }

    private void getNetwork() {
        //TODO: need to add level 1 area
        // List mCountryList = new ArrayList<CountryJsonData>();
        //List mL1List = new ArrayList<CountryJsonData>();

        Disposable RMDisposable = RiskMonitoringService.INSTANCE.readJsonFile()
                .map(JSONObject::new).flatMap(jsonObject -> {
                    return RiskMonitoringService.INSTANCE.mapJasonToCountryData(jsonObject, new Gson());
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(countryJsonData -> {
                            //Timber.d("Country id is: %s, level 1: %s", countryJsonData.getCountryId(), countryJsonData.getLevelOneValues().toString());
                            //  mCountryList.add(countryJsonData.getCountryId());
                            // mL1List.add(countryJsonData.getLevelOneValues().get(2));

                            //  System.out.println("LIST: "+countryJsonData.getLevelOneValues().get(2));
                        }
                );

        compositeDisposable.add(RMDisposable);
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
        if (mReference != null && mValueListener != null){
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
        }

        if(view == btnApprove){
            approveOrReject(true);

        }

        if(view == btnReject){
            approveOrReject(false);
        }

    }

    private void approveOrReject(boolean isApproved) {
      //  DatabaseReference db = mReference;
        Log.e("e:",mReference+" Clicked!");
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(isApproved){
                    mReference.child("approval").child("countryDirector").child(countryID).setValue(Constants. REQ_APPROVED);
                    mReference.child("alertLevel").setValue(Constants.TRIGGER_RED);
                    Intent intent = new Intent(AlertDetailActivity.this, HomeScreen.class);
                    startActivity(intent);
                }else {
                    mReference.child("approval").child("countryDirector").child(countryID).setValue(Constants.REQ_REJECTED);
                }
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

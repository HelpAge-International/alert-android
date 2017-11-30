package org.alertpreparedness.platform.alert.dashboard.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertAdapter;
import org.alertpreparedness.platform.alert.model.Alert;
import org.alertpreparedness.platform.alert.risk_monitoring.model.CountryJsonData;
import org.alertpreparedness.platform.alert.risk_monitoring.service.RiskMonitoringService;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AlertDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtHazardName, txtPopulation, txtAffectedArea, txtInfo, txtLastUpdated, txtActionBarTitle;
    private ImageView imgHazard, imgPopulation, imgAffectedArea, imgInfo, imgClose, imgUpdate;
    private Toolbar toolbar;
    private Alert alert;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static final String EXTRA_ALERT = "extra_alert";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_detail);

        Intent intent = getIntent();
        alert = (Alert) intent.getSerializableExtra(EXTRA_ALERT);

        toolbar = (Toolbar) findViewById(R.id.action_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Change the color of the arrow
        final Drawable upArrow = toolbar.getNavigationIcon();
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        txtHazardName = (TextView) findViewById(R.id.txtHazardName);
        txtPopulation = (TextView) findViewById(R.id.txtPopulationAffected);
        txtAffectedArea = (TextView) findViewById(R.id.txtArea);
        txtInfo = (TextView) findViewById(R.id.txtInfo);
        txtLastUpdated = (TextView) findViewById(R.id.txtLastUpdated);
        txtActionBarTitle = (TextView) findViewById(R.id.action_bar_title);

        imgHazard = (ImageView) findViewById(R.id.imgHazardIcon);
        imgPopulation = (ImageView) findViewById(R.id.imgPopulationIcon);
        imgAffectedArea = (ImageView) findViewById(R.id.imgAreaIcon);
        imgInfo = (ImageView) findViewById(R.id.imgInfo);
        imgClose = (ImageView) findViewById(R.id.leftImageView);
        imgUpdate = (ImageView) findViewById(R.id.rightImageView);

        imgUpdate.setImageResource(R.drawable.ic_create_white_24dp);
        imgUpdate.setOnClickListener(this);
        imgClose.setVisibility(View.GONE);
        fetchDetails();
    }

    public void fetchDetails() {
        Window window = getWindow();
        //getNetwork();
        if(alert.getAlertLevel() == 1){
            toolbar.setBackgroundResource(R.color.alertAmber);
            txtActionBarTitle.setText(R.string.amber_alert_text);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.alertAmber));
            }

        }else if(alert.getAlertLevel() == 2){
            toolbar.setBackgroundResource(R.color.alertRed);
            txtActionBarTitle.setText(R.string.red_alert_text);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.alertRed));
            }
        }

        for(int i = 0; i < Constants.COUNTRIES.length; i++){
            if(alert.getCountry() == i){
                txtAffectedArea.setText(Constants.COUNTRIES[i]);
            }
        }

        for (int i = 0; i < Constants.HAZARD_SCENARIO_NAME.length; i++) {
            if(i == alert.getHazardScenario()){
                AlertAdapter.fetchIcon(Constants.HAZARD_SCENARIO_NAME[i], imgHazard);
                txtHazardName.setText(Constants.HAZARD_SCENARIO_NAME[i]);
                txtPopulation.setText(getPeopleAsString(alert.getPopulation()));
                imgPopulation.setImageResource(R.drawable.alert_population);
                imgAffectedArea.setImageResource(R.drawable.alert_areas);
                imgInfo.setImageResource(R.drawable.alert_information);
                txtLastUpdated.setText(getUpdatedAsString(alert.getUpdated()));
                txtInfo.setText((CharSequence) alert.getInfo());
            }else if(alert.getOtherName() != null ){
                imgHazard.setImageResource(R.drawable.other);
                txtHazardName.setText(alert.getOtherName());
                txtPopulation.setText(getPeopleAsString(alert.getPopulation()));
                imgPopulation.setImageResource(R.drawable.alert_population);
                imgAffectedArea.setImageResource(R.drawable.alert_areas);
                imgInfo.setImageResource(R.drawable.alert_information);
                txtLastUpdated.setText(getUpdatedAsString(alert.getUpdated()));
                txtInfo.setText((CharSequence) alert.getInfo());
            }

        }

    }

    private String getPeopleAsString(long population) {
        return population+" people";
    }

    private SpannableStringBuilder getUpdatedAsString(String updateDate) {
        SpannableStringBuilder sb = new SpannableStringBuilder("Last updated: "+updateDate);
        StyleSpan b = new StyleSpan(android.graphics.Typeface.BOLD);
        sb.setSpan(b,14, 14 + updateDate.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return sb;
    }

    private void getNetwork(){

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    public void onClick(View view) {
        if(view==imgUpdate){
            Intent intent = new Intent(AlertDetailActivity.this, UpdateAlertActivity.class);
            intent.putExtra("alert_name", alert.getHazardScenario());
            intent.putExtra("alert_population", alert.getPopulation());
            intent.putExtra("alert_info", alert.getInfo());
            startActivity(intent);
        }
    }
}

package org.alertpreparedness.platform.alert.dashboard.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertAdapter;
import org.alertpreparedness.platform.alert.model.Alert;
import org.alertpreparedness.platform.alert.utils.Constants;

public class AlertDetailActivity extends AppCompatActivity {

    private TextView txtHazardName, txtPopulation, txtAffectedArea, txtInfo, txtLastUpdated, txtActionBarTitle;
    private ImageView imgHazard, imgPopulation, imgAffectedArea, imgInfo, imgClose, imgUpdate;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_detail);

        Intent intent = getIntent();
        int itemId = intent.getIntExtra("ITEM_ID", 0);

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

        imgClose.setVisibility(View.GONE);
        fetchDetails(itemId);
    }

    public void fetchDetails(int id) {
        final Alert alert = AlertAdapter.getInstance().getAlertList().get(id);

        if(alert.alertLevel == 1){
            toolbar.setBackgroundResource(R.color.alertAmber);
            txtActionBarTitle.setText(R.string.amber_alert_text);
        }else if(alert.alertLevel == 2){
            toolbar.setBackgroundResource(R.color.alertRed);
            txtActionBarTitle.setText(R.string.red_alert_text);
        }

        for (int i = 0; i < Constants.HAZARD_SCENARIO_NAME.length; i++) {
            if(i == alert.getHazardScenario()){
                AlertAdapter.fetchIcon(Constants.HAZARD_SCENARIO_NAME[i], imgHazard);
                txtHazardName.setText(Constants.HAZARD_SCENARIO_NAME[i]);
                txtPopulation.setText(getPeopleAsString(alert.getPopulation()));
                imgPopulation.setImageResource(R.drawable.alert_population);
                imgAffectedArea.setImageResource(R.drawable.alert_areas);
                imgInfo.setImageResource(R.drawable.alert_information);
                txtLastUpdated.setText(getUpdatedAsString(alert.updated));
                System.out.println("Date: "+ alert.updated);
            }else if(alert.getOtherName() != null ){
                imgHazard.setImageResource(R.drawable.other);
                txtHazardName.setText(alert.getOtherName());
                txtPopulation.setText(getPeopleAsString(alert.getPopulation()));
                imgPopulation.setImageResource(R.drawable.alert_population);
                imgAffectedArea.setImageResource(R.drawable.alert_areas);
                imgInfo.setImageResource(R.drawable.alert_information);
                txtLastUpdated.setText(getUpdatedAsString(alert.updated));
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


}

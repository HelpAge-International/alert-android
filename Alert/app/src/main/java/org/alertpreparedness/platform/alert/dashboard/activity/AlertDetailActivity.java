package org.alertpreparedness.platform.alert.dashboard.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertAdapter;
import org.alertpreparedness.platform.alert.model.Alert;
import org.alertpreparedness.platform.alert.utils.Constants;

public class AlertDetailActivity extends AppCompatActivity {

    private TextView txtHazardName, txtPopulation, txtAffectedArea, txtInfo;
    private ImageView imgHazard, imgPopulation, imgAffectedArea, imgInfo;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_detail);

        Intent intent = getIntent();
        int itemId = intent.getIntExtra("ITEM_ID", 0);

        toolbar = (Toolbar) findViewById(R.id.action_toolbar);

        txtHazardName = (TextView) findViewById(R.id.txtHazardName);
        txtPopulation = (TextView) findViewById(R.id.txtPopulationAffected);
        txtAffectedArea = (TextView) findViewById(R.id.txtArea);
        txtInfo = (TextView) findViewById(R.id.txtInfo);

        imgHazard = (ImageView) findViewById(R.id.imgHazardIcon);
        imgPopulation = (ImageView) findViewById(R.id.imgPopulationIcon);
        imgAffectedArea = (ImageView) findViewById(R.id.imgAreaIcon);
        imgInfo = (ImageView) findViewById(R.id.imgInfo);


        fetchDetails(itemId);
    }

    public void fetchDetails(int id) {
        final Alert alert = AlertAdapter.getInstance().getAlertList().get(id);

        for (int i = 0; i < Constants.HAZARD_SCENARIO_NAME.length; i++) {
            if(i == alert.getHazardScenario()){
                txtHazardName.setText(Constants.HAZARD_SCENARIO_NAME[i]);
                txtPopulation.setText(getPeopleAsString(alert.getPopulation()));
            }

        }

    }

    private String getPeopleAsString(long population) {
        return population+" people";
    }


}

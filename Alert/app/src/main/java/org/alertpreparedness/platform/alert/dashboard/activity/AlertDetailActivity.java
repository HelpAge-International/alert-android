package org.alertpreparedness.platform.alert.dashboard.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertAdapter;
import org.alertpreparedness.platform.alert.model.Alert;

public class AlertDetailActivity extends AppCompatActivity {

    private TextView txtHazardName, txtPopulation, txtAffectedArea, txtInfo;
    private ImageView imgHazard, imgPopulation, imgAffectedArea, imgInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_detail);

        Intent intent = getIntent();
        int itemId = intent.getIntExtra("ITEM_ID", 0);

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
        System.out.println("ID: "+id);
    }


}

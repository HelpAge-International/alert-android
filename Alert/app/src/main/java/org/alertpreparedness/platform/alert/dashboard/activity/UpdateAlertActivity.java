package org.alertpreparedness.platform.alert.dashboard.activity;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.model.Alert;
import org.alertpreparedness.platform.alert.utils.Constants;

import static org.alertpreparedness.platform.alert.dashboard.activity.AlertDetailActivity.EXTRA_ALERT;

public class UpdateAlertActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView closeImageView, imgRight, imgAlertColour;
    private TextView mainTextView, txtHazardName, txtHazardColour, txtAffectedArea, txtAddMoreArea, txtInfo;
    private EditText etPopulation;
    private Button btnSaveChanges;
    public Toolbar toolbar;
    private Alert alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_alert);

        Intent intent = getIntent();
        alert = (Alert) intent.getSerializableExtra(EXTRA_ALERT);

        mainTextView = (TextView) findViewById(R.id.action_bar_title);
        closeImageView = (ImageView) findViewById(R.id.leftImageView);
        imgRight = (ImageView) findViewById(R.id.rightImageView);

        toolbar = (Toolbar) findViewById(R.id.action_toolbar);
        setSupportActionBar(toolbar);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);

        mainTextView.setText(R.string.text_update_alert);
        mainTextView.setPadding(72,0,0, 0);

        closeImageView.setImageBitmap(null);
        closeImageView.setBackgroundResource(R.drawable.close);
        closeImageView.setOnClickListener(this);
        imgRight.setVisibility(View.GONE);

        txtHazardName = (TextView) findViewById(R.id.textViewHazardName);
        txtHazardColour = (TextView) findViewById(R.id.textViewAlertColour);
        etPopulation = (EditText) findViewById(R.id.editTextPopulation);
        imgAlertColour = (ImageView) findViewById(R.id.imgAlertColour);
        txtAffectedArea = (TextView) findViewById(R.id.textViewLocation);
        txtAddMoreArea = (TextView) findViewById(R.id.textViewAddMoreArea);
        txtInfo = (TextView) findViewById(R.id.editTextInfo);
        btnSaveChanges = (Button) findViewById(R.id.btnSaveChanges);

        txtHazardColour.setOnClickListener(this);
        txtAddMoreArea.setOnClickListener(this);
        txtAffectedArea.setOnClickListener(this);
        btnSaveChanges.setOnClickListener(this);

        fetchDetails();
        setUpActionBarColour();

    }

    private void fetchDetails() {

        for (int i = 0; i < Constants.HAZARD_SCENARIO_NAME.length; i++) {
            if(i == alert.getHazardScenario()) {
                txtHazardName.setText(Constants.HAZARD_SCENARIO_NAME[i]);

            }
        }

        if(alert.getAlertLevel() == 1){
            imgAlertColour.setImageResource(R.drawable.amber_dot_26dp);
        }else if (alert.getAlertLevel() == 2){
            imgAlertColour.setImageResource(R.drawable.red_dot_26dp);

        }

        for(int i = 0; i < Constants.COUNTRIES.length; i++){
            if(alert.getCountry() == i){
                txtAffectedArea.setText(Constants.COUNTRIES[i]);
            }
        }
    }

    private void setUpActionBarColour() {
        Window window = getWindow();
        if(alert.getAlertLevel() == 1){
            toolbar.setBackgroundResource(R.color.alertAmber);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.alertAmber));
            }

        }else if(alert.getAlertLevel() == 2){
            toolbar.setBackgroundResource(R.color.alertRed);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.alertRed));
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(closeImageView==view){finish();}

        if(txtHazardColour==view){
            Intent intent = new Intent(UpdateAlertActivity.this, AlertTypesActivity.class);
            intent.putExtra(EXTRA_ALERT, alert);
            startActivityForResult(intent, 1);
        }

        if(txtAddMoreArea==view){
            Intent intent = new Intent(UpdateAlertActivity.this, AffectedAreaActivity.class);
            //intent.putExtra("country", alert.getCountry());
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){

        }
    }
}

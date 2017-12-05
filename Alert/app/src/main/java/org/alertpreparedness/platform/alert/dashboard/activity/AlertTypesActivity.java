package org.alertpreparedness.platform.alert.dashboard.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.model.Alert;

import static org.alertpreparedness.platform.alert.dashboard.activity.AlertDetailActivity.EXTRA_ALERT;

public class AlertTypesActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgLeft, imgRight;
    private TextView mainTextView, txtGreen, txtAmber, txtRed;
    private Toolbar toolbar;
    private Alert alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_types);

        Intent intent = getIntent();
        alert = (Alert) intent.getSerializableExtra(EXTRA_ALERT);

        //  Intent data = new Intent();// data.putExtra(EXTRA_ALERT, alert);
        //  setResult(RESULT_OK, data);
        mainTextView = (TextView) findViewById(R.id.action_bar_title);
        txtGreen = (TextView) findViewById(R.id.textViewGreen);
        txtAmber = (TextView) findViewById(R.id.textViewAmber);
        txtRed = (TextView) findViewById(R.id.textViewRed);
        imgLeft = (ImageView) findViewById(R.id.leftImageView);
        imgRight = (ImageView) findViewById(R.id.rightImageView);

        toolbar = (Toolbar) findViewById(R.id.action_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mainTextView.setText(R.string.txt_alert_level);

        imgRight.setVisibility(View.GONE);
        imgLeft.setVisibility(View.GONE);
        txtGreen.setOnClickListener(this);
        txtAmber.setOnClickListener(this);
        txtRed.setOnClickListener(this);

        // Change the color of the arrow
        final Drawable upArrow = toolbar.getNavigationIcon();
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

    }

    @Override
    public void onClick(View view) {
        //if(view==closeImageView){finish();}

        if(view==txtGreen){
            Intent intent = new Intent(AlertTypesActivity.this, UpdateAlertActivity.class);
            intent.putExtra(EXTRA_ALERT, alert);
            intent.putExtra("alert_type", "green");
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}

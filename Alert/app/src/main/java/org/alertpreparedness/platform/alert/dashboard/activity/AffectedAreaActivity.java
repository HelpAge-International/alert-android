package org.alertpreparedness.platform.alert.dashboard.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.model.Alert;

import static org.alertpreparedness.platform.alert.dashboard.activity.AlertDetailActivity.EXTRA_ALERT;

public class AffectedAreaActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgLeft, imgRight;
    private TextView mainTextView, textViewCountry, textViewSave;
    private Toolbar toolbar;
    private Alert alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affected_area);

//        Intent intent = getIntent();
//        alert = (Alert) intent.getSerializableExtra("country");

        mainTextView = (TextView) findViewById(R.id.action_bar_title);
        textViewCountry = (TextView) findViewById(R.id.txtCountry);
        textViewSave = (TextView) findViewById(R.id.textViewSave);
        imgLeft = (ImageView) findViewById(R.id.leftImageView);
        imgRight = (ImageView) findViewById(R.id.rightImageView);

        toolbar = (Toolbar) findViewById(R.id.action_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mainTextView.setText(R.string.txt_affected_area);
        // mainTextView.setPadding(72,0,0, 0);
        imgRight.setVisibility(View.GONE);
        imgLeft.setVisibility(View.GONE);

        textViewCountry.setOnClickListener(this);
        textViewSave.setOnClickListener(this);
        // Change the color of the arrow
        final Drawable upArrow = toolbar.getNavigationIcon();
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onClick(View view) {
        if(view == textViewCountry){
            Intent intent = new Intent(AffectedAreaActivity.this, CountryListActivity.class);
            startActivity(intent);
        }

        if(view == textViewSave) {
            Intent intent = new Intent(AffectedAreaActivity.this, UpdateAlertActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}

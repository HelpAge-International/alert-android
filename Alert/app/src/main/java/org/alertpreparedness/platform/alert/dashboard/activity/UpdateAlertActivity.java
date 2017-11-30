package org.alertpreparedness.platform.alert.dashboard.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;

public class UpdateAlertActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView closeImageView, imgRight;
    public TextView mainTextView, txtHazardName, txtPopulation, txtAffectedArea, txtInfo;
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_alert);

        mainTextView = (TextView) findViewById(R.id.action_bar_title);
        closeImageView = (ImageView) findViewById(R.id.leftImageView);
        imgRight = (ImageView) findViewById(R.id.rightImageView);
        toolbar = (Toolbar) findViewById(R.id.alert_appbar);

        setSupportActionBar(toolbar);
        mainTextView.setText(R.string.text_update_alert);
        mainTextView.setPadding(72,0,0, 0);

        closeImageView.setImageBitmap(null);
        closeImageView.setBackgroundResource(R.drawable.close);
        closeImageView.setOnClickListener(this);
        imgRight.setVisibility(View.GONE);

        Intent intent = getIntent();
        String alertName = intent.getStringExtra("alert_name");

       // Log.e("Tag", alertName);
        System.out.println("Name: "+ alertName);
    }

    @Override
    public void onClick(View view) {
        if(closeImageView==view){finish();}
    }
}

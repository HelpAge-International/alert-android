package org.alertpreparedness.platform.alert.dashboard.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;

public class CreateAlertActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView closeImageView, imgRight;
    public TextView mainTextView;
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alert);

        mainTextView = (TextView) findViewById(R.id.action_bar_title);
        closeImageView = (ImageView) findViewById(R.id.leftImageView);
        imgRight = (ImageView) findViewById(R.id.rightImageView);
        toolbar = (Toolbar) findViewById(R.id.alert_appbar);

        setSupportActionBar(toolbar);
        mainTextView.setText(R.string.text_create_alert);
        mainTextView.setPadding(72,0,0, 0);

        closeImageView.setImageBitmap(null);
        closeImageView.setBackgroundResource(R.drawable.close);
        closeImageView.setOnClickListener(this);
        imgRight.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View view) {
        if(view==closeImageView){finish();}
    }
}

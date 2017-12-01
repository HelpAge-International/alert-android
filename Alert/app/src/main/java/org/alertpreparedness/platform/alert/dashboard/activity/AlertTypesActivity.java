package org.alertpreparedness.platform.alert.dashboard.activity;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.alertpreparedness.platform.alert.R;

public class AlertTypesActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgLeft, imgRight;
    private TextView mainTextView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_types);

        mainTextView = (TextView) findViewById(R.id.action_bar_title);
        imgLeft = (ImageView) findViewById(R.id.leftImageView);
        imgRight = (ImageView) findViewById(R.id.rightImageView);

        toolbar = (Toolbar) findViewById(R.id.action_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mainTextView.setText(R.string.txt_alert_level);
       // mainTextView.setPadding(72,0,0, 0);

        imgRight.setVisibility(View.GONE);
        imgLeft.setVisibility(View.GONE);

        // Change the color of the arrow
        final Drawable upArrow = toolbar.getNavigationIcon();
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

    }

    @Override
    public void onClick(View view) {
        //if(view==closeImageView){finish();}
    }
}

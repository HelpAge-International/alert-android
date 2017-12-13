package org.alertpreparedness.platform.alert.dashboard.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertAdapter;
import org.alertpreparedness.platform.alert.dashboard.adapter.TaskAdapter;
import org.alertpreparedness.platform.alert.helper.DataHandler;
import org.alertpreparedness.platform.alert.home.HomeFragment;
import org.alertpreparedness.platform.alert.interfaces.OnAlertItemClickedListener;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.interfaces.IHomeActivity;
import org.alertpreparedness.platform.alert.interfaces.iRedAlertRequest;
import org.alertpreparedness.platform.alert.model.Alert;
import org.alertpreparedness.platform.alert.model.Tasks;
import org.alertpreparedness.platform.alert.responseplan.ResponsePlanFragment;
import org.alertpreparedness.platform.alert.risk_monitoring.model.CountryJsonData;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

import static org.alertpreparedness.platform.alert.dashboard.activity.AlertDetailActivity.EXTRA_ALERT;


public class HomeScreen extends MainDrawer {

    private static final int STORAGE_RC = 0x0013;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setFragment(new HomeFragment());
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Check for Storage permissions
        int permCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permCheck != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_RC);
            }
        }
    }

}
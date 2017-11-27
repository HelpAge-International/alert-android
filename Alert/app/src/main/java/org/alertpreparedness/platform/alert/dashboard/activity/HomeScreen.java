package org.alertpreparedness.platform.alert.dashboard.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertAdapter;
import org.alertpreparedness.platform.alert.dashboard.adapter.TaskAdapter;
import org.alertpreparedness.platform.alert.helper.DataHandler;
import org.alertpreparedness.platform.alert.helper.OnAlertItemClickedListener;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.model.Alert;
import org.alertpreparedness.platform.alert.model.Tasks;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.risk_monitoring.model.CountryJsonData;
import org.alertpreparedness.platform.alert.risk_monitoring.service.RiskMonitoringService;
import org.alertpreparedness.platform.alert.utils.PreferHelper;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class HomeScreen extends MainDrawer implements View.OnClickListener, OnAlertItemClickedListener {
    private static final int STORAGE_RC = 0x0013;
    private User user;
    private RecyclerView myTaskRecyclerView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private Toolbar toolbar;
    private String[] usersID;
    private Alert alert;
    private ArrayList<CountryJsonData> mCountryList;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
   // private DataHandler dataHandler = new DataHandler();

    public static TaskAdapter taskAdapter;
    public static List<Tasks> tasksList;
    public static TextView appBarTitle;
    public static AlertAdapter alertAdapter;
    public static List<Alert> alertList;
    public static RecyclerView alertRecyclerView;
    public static String countryID, agencyAdminID, systemAdminID, networkCountryID;
    public static final String mypreference = "mypref";
    public static final String userKey = "UserType";
    public static final PreferHelper sharedPreferences = new PreferHelper();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateDrawer(R.layout.activity_home_screen);
        AlertAdapter.updateActivity(this);

        toolbar = (Toolbar) findViewById(R.id.alert_appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        countryID = UserInfo.getUser(this).countryID;
        agencyAdminID = UserInfo.getUser(this).agencyAdminID;
        systemAdminID = UserInfo.getUser(this).systemAdminID;
        networkCountryID = UserInfo.getUser(this).networkCountryID;

        Timber.d("uid: %s", UserInfo.getUser(this).getUserID());

        mCountryList = new ArrayList<CountryJsonData>();

        System.out.println("Network: "+networkCountryID);
        usersID = new String[]{networkCountryID, countryID};

        Disposable RMDisposable = RiskMonitoringService.INSTANCE.readJsonFile()
                .map(fileText -> {
                    return new JSONObject(fileText);
                }).flatMap( jsonObject -> {
                    return RiskMonitoringService.INSTANCE.mapJasonToCountryData(jsonObject, new Gson());
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(countryJsonData -> {
                    Timber.d("Country id is: %s, level 1: %s", countryJsonData.getCountryId(), countryJsonData.getLevelOneValues().size());
                    mCountryList.add(countryJsonData);
                    //System.out.println("LIST: "+mCountryList.get(1));
                }
        );
        compositeDisposable.add(RMDisposable);

        for (int i=0; i<mCountryList.size(); i++){
            System.out.println("LIST: "+mCountryList.get(i).getLevelOneValues());
        }

        appBarTitle = (TextView) findViewById(R.id.custom_bar_title);
        appBarTitle.setOnClickListener(this);

        alertRecyclerView = (RecyclerView) findViewById(R.id.alert_list_view);
        alertRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager alertlayoutManager = new LinearLayoutManager(getApplicationContext());
        alertRecyclerView.setLayoutManager(alertlayoutManager);
        alertRecyclerView.setItemAnimator(new DefaultItemAnimator());
        alertRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

        alertList = new ArrayList<>();
        alertAdapter = new AlertAdapter(alertList);
        alertRecyclerView.setAdapter(alertAdapter);

        myTaskRecyclerView = (RecyclerView) findViewById(R.id.tasks_list_view);
        myTaskRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        myTaskRecyclerView.setLayoutManager(layoutManager);
        myTaskRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myTaskRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

        tasksList = new ArrayList<>();
        taskAdapter = new TaskAdapter(tasksList);
        myTaskRecyclerView.setAdapter(taskAdapter);

        firebaseDatabase = FirebaseDatabase.getInstance();

        for (String ids : usersID) {
            DataHandler.getAlertsFromFirebase(ids);
        }

        for (String ids : usersID) {
            DataHandler.getTasksFromFirebase(ids);
        }

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



        @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        compositeDisposable.dispose();
    }

    @Override
    public void onClick(View view) {
        if (view == appBarTitle) {
            startActivity(new Intent(getApplicationContext(), CreateAlertActivity.class));
        }
    }

    @Override
    public void onAlertItemClicked(int position) {
        Intent intent = new Intent(HomeScreen.this, AlertDetailActivity.class);
        intent.putExtra("ITEM_ID", position);
        startActivity(intent);
    }
}
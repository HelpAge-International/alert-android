package org.alertpreparedness.platform.alert.helper;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.AlertApplication;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseDatabaseRef;
import org.alertpreparedness.platform.alert.dagger.annotation.HazardOtherRef;
import org.alertpreparedness.platform.alert.dagger.annotation.IndicatorRef;
import org.alertpreparedness.platform.alert.dashboard.model.AlertFieldModel;
import org.alertpreparedness.platform.alert.firebase.AlertModel;
import org.alertpreparedness.platform.alert.interfaces.IHomeActivity;
import org.alertpreparedness.platform.alert.dashboard.model.Alert;
import org.alertpreparedness.platform.alert.dashboard.model.Tasks;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.DBListener;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;


/**
 * Created by faizmohideen on 20/11/2017.
 */

public class DataHandler {
    private List<Integer> alerts = new ArrayList<Integer>();
    private DBListener dbListener = new DBListener();
    private Calendar date = Calendar.getInstance();
    private String dateFormat = "dd/MM/yyyy";
    private SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());
    private String countryID, agencyAdminID, systemAdminID, networkCountryID;
    private String[] usersID;
    private Alert alert = new Alert();

    @Inject
    @AlertRef
    DatabaseReference dbAlertRef;

    @Inject
    @ActionRef
    DatabaseReference dbActionRef;

    @Inject
    @IndicatorRef
    DatabaseReference dbIndicatorRef;

    @Inject
    @HazardOtherRef
    DatabaseReference dbHazardOtherRef;


    public DataHandler() {
        DependencyInjector.applicationComponent().inject(this);
    }
}
package org.alertpreparedness.platform.alert.helper;

import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.HazardOtherRef;
import org.alertpreparedness.platform.alert.dagger.annotation.IndicatorRef;
import org.alertpreparedness.platform.alert.dashboard.model.Alert;
import org.alertpreparedness.platform.alert.utils.DBListener;

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
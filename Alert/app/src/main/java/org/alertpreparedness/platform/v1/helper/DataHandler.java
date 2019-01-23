package org.alertpreparedness.platform.v1.helper;

import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.v1.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.AlertRef;
import org.alertpreparedness.platform.v1.dagger.annotation.HazardOtherRef;
import org.alertpreparedness.platform.v1.dagger.annotation.IndicatorRef;
import org.alertpreparedness.platform.v1.dashboard.model.Alert;
import org.alertpreparedness.platform.v1.utils.DBListener;

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
        DependencyInjector.userScopeComponent().inject(this);
    }
}
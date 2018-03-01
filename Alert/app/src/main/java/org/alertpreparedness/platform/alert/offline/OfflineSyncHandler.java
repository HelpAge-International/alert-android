package org.alertpreparedness.platform.alert.offline;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseAlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseIndicatorRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseLogRef;
import org.alertpreparedness.platform.alert.dagger.annotation.IndicatorRef;
import org.alertpreparedness.platform.alert.firebase.IndicatorModel;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.IndicatorsFetcher;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.AppUtils;

import java.util.HashMap;

import javax.inject.Inject;

import timber.log.Timber;


public class OfflineSyncHandler {

    /**
     * Alerts for country office ID (e.g. /sand/alert/<countryId>
     */
    @Inject
    @AlertRef
    DatabaseReference alertRef;

    /**
     * Base alert reference (e.g. /sand/alert/
     */
    @Inject
    @BaseAlertRef
    DatabaseReference baseAlertRef;

    /**
     * Current user's agency (e.g. /sand/agency/<agencyId>) - GET AGENCY - GET NETWORK - GET NETWORK STUFF
     */
    @Inject
    @AgencyRef
    DatabaseReference agencyRef;

    /**
     * Base action reference (e.g. /sand/action)
     */
    @Inject
    @BaseActionRef
    DatabaseReference baseActionRef;

    /**
     * Base indicator reference (e.g. /sand/indicator)
     */
    @Inject
    @BaseIndicatorRef
    DatabaseReference baseIndicatorRef;

    /**
     * Indicators for country office ID (e.g. /sand/indicator/<countryId>
     */
    @Inject
    @IndicatorRef
    DatabaseReference countryIndicatorRef;

    /**
     * Base indicator log reference (e.g. /sand/log)
     */
    @Inject
    @BaseLogRef
    DatabaseReference baseLogRef;

    @Inject
    User user;

    private DatabaseReferenceOfflineHandler databaseReferenceOfflineHandler;

    private static OfflineSyncHandler instance;

    private OfflineSyncHandler() {
    }

    public static OfflineSyncHandler getInstance() {
        if (instance == null) {
            instance = new OfflineSyncHandler();
        }

        return instance;
    }

    public void sync() {

        if (alertRef == null) {
            if (new UserInfo().getUser() != null) {
                DependencyInjector.applicationComponent().inject(this);
            } else {
                return;
            }
        }
        Timber.d("SYNCING....");

        if (databaseReferenceOfflineHandler != null) {
            databaseReferenceOfflineHandler.cancel();
        }

        databaseReferenceOfflineHandler = new DatabaseReferenceOfflineHandler(2);

        // TODO: Figure out if it's worthwhile going online and offline
        // DatabaseReference.goOnline();

        // fetchCountryLevelAlerts();

        // fetchNetworkLevelAlerts();

        // fetchTasks();

        fetchIndicators();

        // fetchIndicatorLogs();

         /* Risk Monitoring */

         /* Response plans */

        // ActiveFragment
    }

    private void fetchTasks() {

    }

    private void fetchIndicators() {
        new IndicatorsFetcher(indicatorSnapshot -> {
            IndicatorModel indicatorModel =
                    AppUtils.getFirebaseModelFromDataSnapshot(indicatorSnapshot, IndicatorModel.class);
            Timber.d("Fetched indicator: %1$s", indicatorModel.getName());
        }).fetch();
    }

    private void fetchIndicatorLogs() {
    }

    private void fetchNetworkLevelAlerts() {
        agencyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Boolean> networks =
                        (HashMap<String, Boolean>) dataSnapshot.child("networks").getValue();

                if (networks != null) {
                    for (String id : networks.keySet()) {

                        DatabaseReference ref = baseAlertRef.child(id);
                        ref.keepSynced(true);
                        ref.addListenerForSingleValueEvent(getValueListenerForOfflineCaching());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseReferenceOfflineHandler.notifyComplete();
                Timber.d("Error downloading offline data: %s", databaseError.getMessage());
            }
        });
    }

    private void fetchCountryLevelAlerts() {
        alertRef.keepSynced(true);
        alertRef.addListenerForSingleValueEvent(getValueListenerForOfflineCaching());
    }

    private EmptyValueEventListener getValueListenerForOfflineCaching() {
        return new EmptyValueEventListener(databaseReferenceOfflineHandler);
    }

    private static class EmptyValueEventListener implements ValueEventListener {

        private DatabaseReferenceOfflineHandler databaseReferenceOfflineHandler;

        public EmptyValueEventListener(DatabaseReferenceOfflineHandler databaseReferenceOfflineHandler) {
            this.databaseReferenceOfflineHandler = databaseReferenceOfflineHandler;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Timber.d("Success: " + dataSnapshot);
            Timber.d("Success Ref: " + dataSnapshot.getRef());
            for (DataSnapshot ch : dataSnapshot.getChildren()) {
                Timber.d("child = " + ch);
            }

            databaseReferenceOfflineHandler.notifyComplete();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.d("Error downloading offline data: " + databaseError.getMessage());
            databaseReferenceOfflineHandler.notifyComplete();
        }
    }

    private static class DatabaseReferenceOfflineHandler {
        private int num;
        private boolean cancelled = false;

        public DatabaseReferenceOfflineHandler(int num) {
            this.num = num;
        }

        public void notifyComplete() {
            notifyComplete(1);
        }

        public void notifyComplete(int amount) {
            num -= amount;
            checkFinished();
        }

        public void cancel() {
            cancelled = true;
        }

        private void checkFinished() {
            if (!cancelled && num <= 0) {
                // TODO: Figure out if it's worthwhile going online and offline
                // DatabaseReference.goOffline();
                Timber.d("Going offline..");
            }
        }
    }
}

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
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.model.User;

import java.util.HashMap;

import javax.inject.Inject;

import timber.log.Timber;


public class OfflineSyncHandler {

    /** Alerts for country ID (e.g. /sand/alert/<countryId> - COUNTRY LEVEL */
    @Inject
    @AlertRef
    DatabaseReference alertRef;

    /** Base alert reference (e.g. /sand/alert/ */
    @Inject
    @BaseAlertRef
    DatabaseReference baseAlertRef;

    /** Current user's agency (e.g. /sand/agency/<agencyId>) - GET AGENCY - GET NETWORK - GET NETWORK STUFF */
    @Inject
    @AgencyRef
    DatabaseReference agencyRef;

    @Inject
    @BaseActionRef
    DatabaseReference baseActionRef;

    @Inject
    @BaseIndicatorRef
    DatabaseReference baseIndicatorRef;

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

        DatabaseReference.goOnline();

        fetchCountryLevelAlerts();

        fetchNetworkLevelAlerts();

        fetchTasks();


        /* Home */

        /* Tasks = indicators and actions */

        // HomeFragment

         /* Risk Monitoring */

         /* Response plans */

        // ActiveFragment
    }

    /**
     * 1. Fetch a snapshot of the user's agency node<br>
     * 2. Fetch the IDs of the networks<br>
     * 3. For each network ID, fetch the actions<br>
     * 4. For each network ID, fetch the indicators<br>
     * */
    private void fetchTasks() {
        ValueEventListener agencyListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Boolean> networks =
                        (HashMap<String, Boolean>) dataSnapshot.child("networks").getValue();

                if (networks != null) {
                    Timber.d("Fetched networks: %s", networks);
                    for (String networkId : networks.keySet()) {
                        baseActionRef.child(networkId).keepSynced(true);
                        baseActionRef
                                .child(networkId)
                                .addListenerForSingleValueEvent(getValueListenerForOfflineCaching());

                        baseIndicatorRef.child(networkId).keepSynced(true);
                        baseIndicatorRef
                                .child(networkId)
                                .addListenerForSingleValueEvent(getValueListenerForOfflineCaching());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.d("Error downloading offline data: %s", databaseError.getMessage());
            }
        };

        agencyRef.addListenerForSingleValueEvent(agencyListener);
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
                DatabaseReference.goOffline();
                Timber.d("Going offline..");
            }
        }
    }
}

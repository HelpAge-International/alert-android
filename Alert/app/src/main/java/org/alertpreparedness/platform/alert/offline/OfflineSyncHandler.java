package org.alertpreparedness.platform.alert.offline;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseAlertRef;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.model.User;

import java.util.HashMap;

import javax.inject.Inject;

import timber.log.Timber;


public class OfflineSyncHandler {

    @Inject
    @AlertRef
    DatabaseReference alertRef;

    @Inject
    @BaseAlertRef
    DatabaseReference baseAlertRef;

    @Inject
    @AgencyRef
    DatabaseReference agencyRef;

    private DatabaseReferenceOfflineHandler databaseReferenceOfflineHandler;

    private static OfflineSyncHandler instance;

    private OfflineSyncHandler() {
    }

    public static OfflineSyncHandler getInstance(){
        if(instance == null){
            instance = new OfflineSyncHandler();
        }

        return instance;
    }

    public void sync() {

        if(alertRef == null){
            if(new UserInfo().getUser() != null){
                DependencyInjector.applicationComponent().inject(this);
            }
            else{
                return;
            }
        }
        Timber.d("SYNCING....");

        if(databaseReferenceOfflineHandler != null){
            databaseReferenceOfflineHandler.cancel();
        }

        databaseReferenceOfflineHandler = new DatabaseReferenceOfflineHandler(2);

        DatabaseReference.goOnline();
        alertRef.keepSynced(true);
        alertRef.addListenerForSingleValueEvent(new EmptyValueEventListener(databaseReferenceOfflineHandler));

        agencyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Boolean> networks = (HashMap<String, Boolean>) dataSnapshot.child("networks").getValue();

                if (networks != null) {
                    for (String id : networks.keySet()) {

                        DatabaseReference ref = baseAlertRef.child(id);
                        ref.keepSynced(true);
                        ref.addListenerForSingleValueEvent(new EmptyValueEventListener(databaseReferenceOfflineHandler));

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseReferenceOfflineHandler.notifyComplete();
                Timber.d("Error downloading offline data: " + databaseError.getMessage());
            }
        });
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
                System.out.println("ch = " + ch);
            }

            databaseReferenceOfflineHandler.notifyComplete();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.d("Error downloading offline data: " + databaseError.getMessage());
            databaseReferenceOfflineHandler.notifyComplete();
        }
    }

    private static class DatabaseReferenceOfflineHandler{
        private int num;
        private boolean cancelled = false;

        public DatabaseReferenceOfflineHandler(int num) {
            this.num = num;
        }

        public void notifyComplete(){
            notifyComplete(1);
        }

        public void notifyComplete(int amount){
            num -= amount;
            checkFinished();
        }

        public void cancel(){
            cancelled = true;
        }

        private void checkFinished() {
            if(!cancelled && num <= 0){
                DatabaseReference.goOffline();
                Timber.d("Going offline..");
            }
        }


    }
}

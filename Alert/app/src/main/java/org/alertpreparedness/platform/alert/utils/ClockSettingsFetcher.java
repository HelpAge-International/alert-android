package org.alertpreparedness.platform.alert.utils;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.CountryOfficeRef;
import org.alertpreparedness.platform.alert.firebase.ClockSetting;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.model.User;

import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.Observable;

/**
 * Created by Tj on 01/03/2018.
 */

public class ClockSettingsFetcher {

    private ClockSettingsRetrievedListener listener;

    @Inject
    @CountryOfficeRef
    DatabaseReference countryOffice;

    @Inject
    User user;

    @Deprecated
    public ClockSettingsFetcher(ClockSettingsRetrievedListener listener) {
        this.listener = listener;
        DependencyInjector.applicationComponent().inject(this);
    }

    public ClockSettingsFetcher() {
        DependencyInjector.applicationComponent().inject(this);
    }

    @Deprecated
    public void fetch() {
        countryOffice.child(user.agencyAdminID).child(user.countryID).child("clockSettings").child("preparedness").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long durationType = (Long) dataSnapshot.child("durationType").getValue();
                Long value = (Long) dataSnapshot.child("value").getValue();
                if(value == null) {
                    value = 1L;
                }
                listener.onClockSettingsRetrieved(value, durationType);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public Observable<ClockSettingsModel> rxFetch() {

        DatabaseReference ref = countryOffice
                .child(user.agencyAdminID)
                .child(user.countryID)
                .child("clockSettings")
                .child("preparedness");

        return RxFirebaseDatabase.observeSingleValueEvent(ref, ClockSettingsModel.class).toObservable();

    }

    public interface ClockSettingsRetrievedListener {
        void onClockSettingsRetrieved(Long value, Long durationType);
    }

    public class ClockSettingsModel {
        private final Long value;
        private final Long durationType;

        public ClockSettingsModel(Long value, Long durationType) {

            this.value = value;
            this.durationType = durationType;
        }

        public Long getValue() {
            return value;
        }

        public Long getDurationType() {
            return durationType;
        }
    }
}

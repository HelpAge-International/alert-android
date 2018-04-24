package org.alertpreparedness.platform.alert.notifications;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.CountryOfficeRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ResponsePlansRef;
import org.alertpreparedness.platform.alert.firebase.ClockSetting;
import org.alertpreparedness.platform.alert.firebase.ResponsePlanModel;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.AppUtils;
import org.alertpreparedness.platform.alert.utils.SynchronizedCounter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class ResponsePlanFetcher implements SynchronizedCounter.SynchronizedCounterListener {

    private ResponsePlanFetcherListener listener;

    @Inject
    public User user;

    @Inject
    @ResponsePlansRef
    public DatabaseReference baseResponsePlanRef;

    @Inject
    @CountryOfficeRef
    public DatabaseReference countryOfficeRef;

    private boolean failed = false;

    private ResponsePlanFetcherResult responsePlanFetcherResult = new ResponsePlanFetcherResult();
    private SynchronizedCounter responsePlanCounter;

    public enum ResponsePlanType {
        NETWORK_COUNTRY,
        LOCAL_NETWORK,
        COUNTRY
    }

    public ResponsePlanFetcher(ResponsePlanFetcherListener listener) {
        this.listener = listener;
        DependencyInjector.userScopeComponent().inject(this);
    }

    public void fetch(){
        responsePlanFetcherResult = new ResponsePlanFetcher.ResponsePlanFetcherResult();

        responsePlanCounter = new SynchronizedCounter(2);
        responsePlanCounter.addListener(this);

        baseResponsePlanRef.addValueEventListener(new ResponsePlanFetcher.ResponsePlanListener(baseResponsePlanRef, ResponsePlanFetcher.ResponsePlanType.COUNTRY, user.getCountryID(), responsePlanFetcherResult, responsePlanCounter));
        countryOfficeRef.child("clockSettings").child("responsePlans").addValueEventListener(new ResponsePlanFetcher.ClockSettingsListener(countryOfficeRef, ResponsePlanFetcher.ResponsePlanType.COUNTRY, responsePlanFetcherResult, responsePlanCounter));
    }

    @Override
    public void counterChanged(SynchronizedCounter synchronizedCounter, int amount) {
        if(synchronizedCounter == responsePlanCounter && amount == 0){
            notifySuccess();
        }
    }

    private void notifySuccess() {
        listener.responsePlanFetchSuccess(responsePlanFetcherResult);
    }

    public interface ResponsePlanFetcherListener{
        void responsePlanFetchSuccess(ResponsePlanFetcherResult responsePlanFetcherResult);
        void responsePlanFetchFail();
    }

    private class ResponsePlanListener implements ValueEventListener {
        private final String groupId;
        private final ResponsePlanFetcherResult responsePlanFetcherResult;
        private final SynchronizedCounter responsePlanCounter;
        private final ResponsePlanType responsePlanType;
        private final DatabaseReference dbRef;

        public ResponsePlanListener(DatabaseReference dbRef, ResponsePlanType responsePlanType, String groupId, ResponsePlanFetcherResult responsePlanFetcherResult, SynchronizedCounter responsePlanCounter) {
            this.groupId = groupId;
            this.responsePlanType = responsePlanType;
            this.responsePlanFetcherResult = responsePlanFetcherResult;
            this.responsePlanCounter = responsePlanCounter;
            this.dbRef = dbRef;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot)
        {
            dbRef.removeEventListener(this);
            if(dataSnapshot != null){
                for(DataSnapshot responsePlanSnap : dataSnapshot.getChildren()){
                    Timber.d(dataSnapshot.toString());
                    Timber.d(dataSnapshot.getRef().toString());
                    responsePlanFetcherResult.getModels().add(new ResponsePlanFetcherModel(responsePlanType, AppUtils.getValueFromDataSnapshot(responsePlanSnap, ResponsePlanModel.class), groupId, responsePlanSnap.getKey()));
                }
            }
            responsePlanCounter.decrement();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.d("ERROR");
            notifyFailed();
        }
    }

    private void notifyFailed() {
        if(!failed) {
            failed = true;
            listener.responsePlanFetchFail();
        }
    }

    public class ResponsePlanFetcherResult{
        private List<ResponsePlanFetcherModel> models = new ArrayList<>();
        private ClockSetting countryClockSettings;

        public List<ResponsePlanFetcherModel> getModels() {
            return models;
        }

        public void setModels(List<ResponsePlanFetcherModel> models) {
            this.models = models;
        }

        public ClockSetting getCountryClockSettings() {
            return countryClockSettings;
        }

        public void setCountryClockSettings(ClockSetting countryClockSettings) {
            this.countryClockSettings = countryClockSettings;
        }
    }

    public class ResponsePlanFetcherModel {
        private ResponsePlanType responsePlanType;
        private ResponsePlanModel responsePlan;
        private String groupId;
        private String responsePlanId;

        public ResponsePlanFetcherModel(ResponsePlanType responsePlanType, ResponsePlanModel responsePlan, String groupId, String responsePlanId) {
            this.responsePlanType = responsePlanType;
            this.responsePlan = responsePlan;
            this.groupId = groupId;
            this.responsePlanId = responsePlanId;
        }

        public ResponsePlanModel getResponsePlan() {
            return responsePlan;
        }

        public String getResponsePlanId() {
            return responsePlanId;
        }

        public String getGroupId() {
            return groupId;
        }

        public ResponsePlanType getResponsePlanType() {
            return responsePlanType;
        }
    }

    private class ClockSettingsListener implements ValueEventListener {
        private final ResponsePlanType responsePlanType;
        private final ResponsePlanFetcherResult responsePlanFetcherResult;
        private final SynchronizedCounter responsePlanCounter;
        private final DatabaseReference dbRef;

        public ClockSettingsListener(DatabaseReference dbRef, ResponsePlanType responsePlanType, ResponsePlanFetcherResult responsePlanFetcherResult, SynchronizedCounter responsePlanCounter) {
            this.responsePlanType = responsePlanType;
            this.responsePlanFetcherResult = responsePlanFetcherResult;
            this.responsePlanCounter = responsePlanCounter;
            this.dbRef = dbRef;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            ClockSetting clockSetting = AppUtils.getValueFromDataSnapshot(dataSnapshot, ClockSetting.class);

            switch (responsePlanType) {
                case COUNTRY:
                    responsePlanFetcherResult.setCountryClockSettings(clockSetting);
                    break;
            }

            responsePlanCounter.decrement();
            dbRef.removeEventListener(this);

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            notifyFailed();
        }
    }
}

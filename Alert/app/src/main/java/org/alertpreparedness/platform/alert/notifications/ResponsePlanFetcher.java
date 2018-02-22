package org.alertpreparedness.platform.alert.notifications;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.CountryOfficeRef;
import org.alertpreparedness.platform.alert.dagger.annotation.LocalNetworkRef;
import org.alertpreparedness.platform.alert.dagger.annotation.NetworkCountryRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ResponsePlansRef;
import org.alertpreparedness.platform.alert.firebase.ActionModel;
import org.alertpreparedness.platform.alert.firebase.ClockSetting;
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

    @Inject
    @LocalNetworkRef
    public DatabaseReference localNetworkRef;

    @Inject
    @NetworkCountryRef
    public DatabaseReference networkCountryRef;
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
        DependencyInjector.applicationComponent().inject(this);
    }

    public void fetchAll(){
        fetch(true, true, true);
    }

    public void fetch(boolean country, boolean networkCountry, boolean localNetwork){
        responsePlanFetcherResult = new ResponsePlanFetcherResult();

        responsePlanCounter = new SynchronizedCounter(
                    (country ? 2 : 0) +
                        (networkCountry ? 2 : 0) +
                        (localNetwork ? 2 : 0)
        );
        responsePlanCounter.addListener(this);

        if(country) {
            baseResponsePlanRef.child(user.getCountryID()).orderByChild("asignee").equalTo(user.getUserID()).addValueEventListener(new ResponsePlanListener(baseResponsePlanRef, ResponsePlanType.COUNTRY, user.getCountryID(), responsePlanFetcherResult, responsePlanCounter));
            countryOfficeRef.child("clockSettings").child("preparedness").addValueEventListener(new ClockSettingsListener(countryOfficeRef, ResponsePlanType.COUNTRY, responsePlanFetcherResult, responsePlanCounter));
        }

        if(networkCountry) {
            baseResponsePlanRef.child(user.getNetworkCountryID()).orderByChild("asignee").equalTo(user.getUserID()).addValueEventListener(new ResponsePlanListener(baseResponsePlanRef, ResponsePlanType.NETWORK_COUNTRY, user.getNetworkCountryID(), responsePlanFetcherResult, responsePlanCounter));
            networkCountryRef.child("clockSettings").child("preparedness").addValueEventListener(new ClockSettingsListener(networkCountryRef, ResponsePlanType.NETWORK_COUNTRY, responsePlanFetcherResult, responsePlanCounter));
        }

        if(localNetwork) {
            baseResponsePlanRef.child(user.getLocalNetworkID()).orderByChild("asignee").equalTo(user.getUserID()).addValueEventListener(new ResponsePlanListener(baseResponsePlanRef, ResponsePlanType.LOCAL_NETWORK, user.getLocalNetworkID(), responsePlanFetcherResult, responsePlanCounter));
            localNetworkRef.child("clockSettings").child("preparedness").addValueEventListener(new ClockSettingsListener(localNetworkRef, ResponsePlanType.LOCAL_NETWORK, responsePlanFetcherResult, responsePlanCounter));
        }
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
        public void onDataChange(DataSnapshot dataSnapshot) {
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
        private ClockSetting networkCountryClockSettings;
        private ClockSetting localNetworkClockSettings;
        private ClockSetting countryClockSettings;

        public List<ResponsePlanFetcherModel> getModels() {
            return models;
        }

        public void setModels(List<ResponsePlanFetcherModel> models) {
            this.models = models;
        }

        public ClockSetting getNetworkCountryClockSettings() {
            return networkCountryClockSettings;
        }

        public void setNetworkCountryClockSettings(ClockSetting networkCountryClockSettings) {
            this.networkCountryClockSettings = networkCountryClockSettings;
        }

        public ClockSetting getLocalNetworkClockSettings() {
            return localNetworkClockSettings;
        }

        public void setLocalNetworkClockSettings(ClockSetting localNetworkClockSettings) {
            this.localNetworkClockSettings = localNetworkClockSettings;
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
            dbRef.removeEventListener(this);
            ClockSetting clockSetting = dataSnapshot.getValue(ClockSetting.class);

            switch (responsePlanType) {

                case NETWORK_COUNTRY:
                    responsePlanFetcherResult.setNetworkCountryClockSettings(clockSetting);
                    break;
                case LOCAL_NETWORK:
                    responsePlanFetcherResult.setLocalNetworkClockSettings(clockSetting);
                    break;
                case COUNTRY:
                    responsePlanFetcherResult.setCountryClockSettings(clockSetting);
                    break;
            }

            responsePlanCounter.decrement();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            notifyFailed();
        }
    }
}

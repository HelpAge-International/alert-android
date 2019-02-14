package org.alertpreparedness.platform.v1.notifications;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseNetworkCountryRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseNetworkRef;
import org.alertpreparedness.platform.v1.dagger.annotation.CountryOfficeRef;
import org.alertpreparedness.platform.v1.firebase.ActionModel;
import org.alertpreparedness.platform.v1.firebase.ClockSetting;
import org.alertpreparedness.platform.v1.firebase.data_fetchers.NetworkFetcher;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.utils.AppUtils;
import org.alertpreparedness.platform.v1.utils.SynchronizedCounter;
import timber.log.Timber;

@Deprecated
public class ActionFetcher implements SynchronizedCounter.SynchronizedCounterListener {

    private ActionFetcherListener listener;

    @Inject
    public User user;

    @Inject
    @BaseActionRef
    public DatabaseReference baseActionRef;

    @Inject
    @CountryOfficeRef
    public DatabaseReference countryOfficeRef;

    @Inject
    @BaseNetworkCountryRef
    public DatabaseReference baseNetworkCountryRef;

    @Inject
    @BaseNetworkRef
    public DatabaseReference baseNetworkRef;


    private boolean failed = false;

    private ActionFetcherResult actionFetcherResult = new ActionFetcherResult();
    private SynchronizedCounter actionCounter;

    public enum ActionType {
        NETWORK_COUNTRY,
        LOCAL_NETWORK,
        COUNTRY
    }

    public ActionFetcher(ActionFetcherListener listener) {
        this.listener = listener;
        DependencyInjector.userScopeComponent().inject(this);
    }

    public void fetchAll(){
        fetch(true, true, true);
    }

    public void fetch(boolean country, boolean networkCountry, boolean localNetwork){
        actionFetcherResult = new ActionFetcherResult();

        actionCounter = new SynchronizedCounter(
                (country ? 2 : 0) + ((networkCountry || localNetwork) ? 1 : 0)

        );
        actionCounter.addListener(this);

        if(country) {
            baseActionRef.child(user.getCountryID()).orderByChild("asignee").equalTo(user.getUserID()).addValueEventListener(new ActionListener(baseActionRef, ActionType.COUNTRY, user.getCountryID(), actionFetcherResult, actionCounter));
            countryOfficeRef.child("mClockSetting").child("preparedness").addValueEventListener(
                    new ClockSettingsListener(countryOfficeRef, user.getCountryID(), ActionType.COUNTRY,
                            actionFetcherResult, actionCounter));
        }

        new NetworkFetcher(networkFetcherResult -> {
            if(networkCountry) {
                actionCounter.increment(networkFetcherResult.getNetworksCountries().size() * 2);
                for(String networkCountryId : networkFetcherResult.getNetworksCountries()) {
                    baseActionRef.child(networkCountryId).orderByChild("asignee").equalTo(user.getUserID()).addValueEventListener(new ActionListener(baseActionRef, ActionType.NETWORK_COUNTRY, networkCountryId, actionFetcherResult, actionCounter));
                    baseNetworkCountryRef.child("mClockSetting").child("preparedness").addValueEventListener(
                            new ClockSettingsListener(countryOfficeRef, networkCountryId, ActionType.COUNTRY,
                                    actionFetcherResult, actionCounter));
                }
            }

            if(localNetwork) {
                actionCounter.increment(networkFetcherResult.getLocalNetworks().size() * 2);
                for(String localNetworkId : networkFetcherResult.getLocalNetworks()) {
                    baseActionRef.child(localNetworkId).orderByChild("asignee").equalTo(user.getUserID()).addValueEventListener(new ActionListener(baseActionRef, ActionType.LOCAL_NETWORK, localNetworkId, actionFetcherResult, actionCounter));
                    baseNetworkRef.child("mClockSetting").child("preparedness").addValueEventListener(
                            new ClockSettingsListener(countryOfficeRef, localNetworkId, ActionType.LOCAL_NETWORK,
                                    actionFetcherResult, actionCounter));
                }
            }

            actionCounter.decrement();
        }).fetch();

    }

    @Override
    public void counterChanged(SynchronizedCounter synchronizedCounter, int amount) {
        if(synchronizedCounter == actionCounter && amount == 0){
            notifySuccess();
        }
    }

    private void notifySuccess() {
        listener.actionFetchSuccess(actionFetcherResult);
    }

    public interface ActionFetcherListener{
        void actionFetchSuccess(ActionFetcherResult actionFetcherResult);
        void actionFetchFail();
    }

    private class ActionListener implements ValueEventListener {
        private final String groupId;
        private final ActionFetcherResult actionFetcherResult;
        private final SynchronizedCounter actionCounter;
        private final ActionType actionType;
        private final DatabaseReference dbRef;

        public ActionListener(DatabaseReference dbRef, ActionType actionType, String groupId, ActionFetcherResult actionFetcherResult, SynchronizedCounter actionCounter) {
            this.groupId = groupId;
            this.actionType = actionType;
            this.actionFetcherResult = actionFetcherResult;
            this.actionCounter = actionCounter;
            this.dbRef = dbRef;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            dbRef.removeEventListener(this);
            if(dataSnapshot != null){
                for(DataSnapshot actionSnap : dataSnapshot.getChildren()){
                    Timber.d(dataSnapshot.toString());
                    Timber.d(dataSnapshot.getRef().toString());
                    actionFetcherResult.getModels().add(new ActionFetcherModel(actionType, AppUtils.getValueFromDataSnapshot(actionSnap, ActionModel.class), groupId, actionSnap.getKey()));
                }
            }
            actionCounter.decrement();
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
            listener.actionFetchFail();
        }
    }

    public class ActionFetcherResult{
        private List<ActionFetcherModel> models = new ArrayList<>();
        private Map<String, ClockSetting> networkCountryClockSettings = new HashMap<>();
        private Map<String, ClockSetting> localNetworkClockSettings = new HashMap<>();
        private Map<String, ClockSetting> countryClockSettings = new HashMap<>();

        public List<ActionFetcherModel> getModels() {
            return models;
        }

        public void setModels(List<ActionFetcherModel> models) {
            this.models = models;
        }

        public ClockSetting getNetworkCountryClockSettings(String id) {
            return networkCountryClockSettings.get(id);
        }

        public void setNetworkCountryClockSettings(String id, ClockSetting networkCountryClockSettings) {
            this.networkCountryClockSettings.put(id, networkCountryClockSettings);
        }

        public ClockSetting getLocalNetworkClockSettings(String id) {
            return localNetworkClockSettings.get(id);
        }

        public void setLocalNetworkClockSettings(String id, ClockSetting localNetworkClockSettings) {
            this.localNetworkClockSettings.put(id, localNetworkClockSettings);
        }

        public ClockSetting getCountryClockSettings(String id) {
            return countryClockSettings.get(id);
        }

        public void setCountryClockSettings(String id, ClockSetting countryClockSettings) {
            this.countryClockSettings.put(id, countryClockSettings);
        }
    }

    public class ActionFetcherModel {
        private ActionType actionType;
        private ActionModel action;
        private String groupId;
        private String actionId;

        public ActionFetcherModel(ActionType actionType, ActionModel action, String groupId, String actionId) {
            this.actionType = actionType;
            this.action = action;
            this.groupId = groupId;
            this.actionId = actionId;
        }

        public ActionModel getAction() {
            return action;
        }

        public String getActionId() {
            return actionId;
        }

        public String getGroupId() {
            return groupId;
        }

        public ActionType getActionType() {
            return actionType;
        }
    }

    private class ClockSettingsListener implements ValueEventListener {
        private final ActionType actionType;
        private final ActionFetcherResult actionFetcherResult;
        private final SynchronizedCounter actionCounter;
        private final DatabaseReference dbRef;
        private final String id;

        public ClockSettingsListener(DatabaseReference dbRef, String id, ActionType actionType, ActionFetcherResult actionFetcherResult, SynchronizedCounter actionCounter) {
            this.actionType = actionType;
            this.actionFetcherResult = actionFetcherResult;
            this.actionCounter = actionCounter;
            this.dbRef = dbRef;
            this.id = id;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            dbRef.removeEventListener(this);
            ClockSetting clockSetting = dataSnapshot.getValue(ClockSetting.class);

            switch (actionType) {

                case NETWORK_COUNTRY:
                    actionFetcherResult.setNetworkCountryClockSettings(id, clockSetting);
                    break;
                case LOCAL_NETWORK:
                    actionFetcherResult.setLocalNetworkClockSettings(id, clockSetting);
                    break;
                case COUNTRY:
                    actionFetcherResult.setCountryClockSettings(id, clockSetting);
                    break;
            }

            actionCounter.decrement();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            notifyFailed();
        }
    }
}

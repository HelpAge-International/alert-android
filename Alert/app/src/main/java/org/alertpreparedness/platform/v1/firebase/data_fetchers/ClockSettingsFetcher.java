package org.alertpreparedness.platform.v1.firebase.data_fetchers;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Pair;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseCountryOfficeRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseNetworkCountryRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseNetworkRef;
import org.alertpreparedness.platform.v1.dagger.annotation.CountryOfficeRef;
import org.alertpreparedness.platform.v1.firebase.ClockSetting;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * Created by Tj on 01/03/2018.
 */

public class ClockSettingsFetcher {

    private ClockSettingsRetrievedListener listener;

    public static final String TYPE_PREPAREDNESS = "preparedness";
    public static final String TYPE_RESPONSE_PLANS= "responsePlans";
    public static final String TYPE_RISK_MONITORING = "riskMonitoring";

    @Inject
    @CountryOfficeRef
    DatabaseReference countryOfficeRef;

    @Inject
    @BaseCountryOfficeRef
    DatabaseReference baseCountryOfficeRef;

    @Inject
    @BaseNetworkCountryRef
    public DatabaseReference baseNetworkCountryRef;

    @Inject
    @BaseNetworkRef
    public DatabaseReference baseNetworkRef;

    @Inject
    Flowable<NetworkFetcher.NetworkFetcherResult> networkResultFlowable;

    @Inject
    User user;

    @Deprecated
    public ClockSettingsFetcher(ClockSettingsRetrievedListener listener) {
        this.listener = listener;
        DependencyInjector.userScopeComponent().inject(this);
    }

    public ClockSettingsFetcher() {
        DependencyInjector.userScopeComponent().inject(this);
    }

    @Deprecated
    public void fetch() {
        countryOfficeRef.child(user.agencyAdminID).child(user.countryID).child("clockSettings").child("preparedness").addListenerForSingleValueEvent(new ValueEventListener() {
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

    public Flowable<ClockSetting> rxFetchCountry(String countryId, String key){
        return rxFetch(baseCountryOfficeRef.child(user.agencyAdminID).child(countryId), key);
    }

    public Flowable<ClockSetting> rxFetchNetwork(String networkId, String key){
        return rxFetch(baseNetworkRef.child(networkId), key);
    }

    public Flowable<ClockSetting> rxFetchNetworkCountry(String networkId, String networkCountryId, String key){
        return rxFetch(baseNetworkCountryRef.child(networkId).child(networkCountryId), key);
    }

    private Flowable<ClockSetting> rxFetch(DatabaseReference dbRef, String key){
        return RxFirebaseDatabase.observeValueEvent(dbRef.child("clockSettings").child(key))
                .map(dataSnapshot -> AppUtils.getValueFromDataSnapshot(dataSnapshot, ClockSetting.class));
    }

    public Flowable<ClockSettingsResult> rxFetch(String key) {
        return networkResultFlowable.flatMap(networkFetcherResult -> {
            Flowable<ClockSetting> countryFlow = rxFetchCountry(user.countryID, key);

            List<Flowable<Pair<String, ClockSetting>>> localNetworkFlows = new ArrayList<>();
            List<Flowable<Pair<String, ClockSetting>>> globalNetworkFlows = new ArrayList<>();
            List<Flowable<Pair<String, ClockSetting>>> networkCountryFlows = new ArrayList<>();

            for (String networkCountryId : networkFetcherResult.getNetworksCountries()) {
                networkCountryFlows.add(
                        rxFetchNetworkCountry(networkFetcherResult.getNetworkCountryNetworks().get(networkCountryId), networkCountryId, key)
                            .map(clockSetting -> new Pair<>(networkCountryId, clockSetting))
                );
            }
            for (String localNetworkId : networkFetcherResult.getLocalNetworks()) {
                localNetworkFlows.add(
                        rxFetchNetwork(localNetworkId, key)
                            .map(clockSetting -> new Pair<>(localNetworkId, clockSetting))
                );
            }
            for (String globalNetworkId : networkFetcherResult.getGlobalNetworks()) {
                globalNetworkFlows.add(
                        rxFetchNetwork(globalNetworkId, key)
                            .map(clockSetting -> new Pair<>(globalNetworkId, clockSetting))
                );
            }

            Flowable<Map<String, ClockSetting>> networkCountryMapFlow = networkCountryFlows.size() == 0 ? Flowable.just(new HashMap<>()) : Flowable.combineLatest(networkCountryFlows, AppUtils::combinePairToMap);
            Flowable<Map<String, ClockSetting>> globalNetworkMapFlow = globalNetworkFlows.size() == 0 ? Flowable.just(new HashMap<>()) : Flowable.combineLatest(globalNetworkFlows, AppUtils::combinePairToMap);
            Flowable<Map<String, ClockSetting>> localNetworkMapFlow = localNetworkFlows.size() == 0 ? Flowable.just(new HashMap<>()) : Flowable.combineLatest(localNetworkFlows, AppUtils::combinePairToMap);

            return Flowable.combineLatest(countryFlow, localNetworkMapFlow, globalNetworkMapFlow, networkCountryMapFlow, ClockSettingsResult::new);
        });
    }

    public Observable<ClockSettingsModel> rxFetchGroup(String type) {

        DatabaseReference ref = countryOfficeRef
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

    public class ClockSettingsResult{
        private final Map<String, ClockSetting> networkCountryClockSettings;
        private final Map<String, ClockSetting> localNetworkClockSettings;
        private final Map<String, ClockSetting> globalNetworkClockSettings;
        private final ClockSetting countryClockSettings;

        public ClockSettingsResult(ClockSetting countryClockSettings, Map<String, ClockSetting> localNetworkClockSettings, Map<String, ClockSetting> globalNetworkClockSettings, Map<String, ClockSetting> networkCountryClockSettings) {
            this.networkCountryClockSettings = networkCountryClockSettings;
            this.localNetworkClockSettings = localNetworkClockSettings;
            this.globalNetworkClockSettings = globalNetworkClockSettings;
            this.countryClockSettings = countryClockSettings;
        }

        public Map<String, ClockSetting> getNetworkCountryClockSettings() {
            return networkCountryClockSettings;
        }

        public Map<String, ClockSetting> getLocalNetworkClockSettings() {
            return localNetworkClockSettings;
        }


        public Map<String, ClockSetting> getGlobalNetworkClockSettings() {
            return globalNetworkClockSettings;
        }

        public Map<String, ClockSetting> all() {
            HashMap<String, ClockSetting> res = new HashMap<>();
            res.putAll(networkCountryClockSettings);
            res.putAll(localNetworkClockSettings);
            res.put(user.countryID, countryClockSettings);
            res.putAll(globalNetworkClockSettings);
            return res;
        }


        public ClockSetting getCountryClockSettings() {
            return countryClockSettings;
        }
    }

}

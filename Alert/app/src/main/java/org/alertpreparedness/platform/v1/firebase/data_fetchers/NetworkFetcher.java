package org.alertpreparedness.platform.v1.firebase.data_fetchers;

import android.text.TextUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.CountryOfficeRef;
import org.alertpreparedness.platform.v1.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.Flowable;

public class NetworkFetcher implements ValueEventListener {

    @Inject
    @CountryOfficeRef
    DatabaseReference countryOfficeRef;

    private NetworkFetcherListener networkFetcherListener;

    public NetworkFetcher(NetworkFetcherListener networkFetcherListener){
        this.networkFetcherListener = networkFetcherListener;
        DependencyInjector.userScopeComponent().inject(this);
    }

    public NetworkFetcher() {
        DependencyInjector.userScopeComponent().inject(this);
    }

    @Deprecated
    public void fetch(){
        countryOfficeRef.addListenerForSingleValueEvent(this);
    }

    public Flowable<NetworkFetcherResult> rxFetch() {
        return RxFirebaseDatabase.observeValueEvent(countryOfficeRef).map(countryOffice -> {
            HashMap<String, Boolean> localNetworks = countryOffice.child("localNetworks").exists() ? (HashMap<String, Boolean>) countryOffice.child("localNetworks").getValue() : new HashMap<>();

            List<String> globalNetworks = new ArrayList<>();
            List<String> networkCountries = new ArrayList<>();
            Map<String, String> networkCountryNetworks = new HashMap<>();

            for (DataSnapshot network : countryOffice.child("networks").getChildren()) {
                globalNetworks.add(network.getKey());

                String networkCountryId = (String) network.child("networkCountryId").getValue();
                networkCountries.add(networkCountryId);
                networkCountryNetworks.put(networkCountryId, network.getKey());
            }

            ArrayList<String> localNetworkArray = new ArrayList<>();

            assert localNetworks != null;
            if(localNetworks.size() > 0) {
                localNetworkArray = new ArrayList<String>(localNetworks.keySet());
            }
            return new NetworkFetcherResult(localNetworkArray, globalNetworks, networkCountries, networkCountryNetworks);
        });
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        //noinspection unchecked
        HashMap<String, Boolean> localNetworks = dataSnapshot.child("localNetworks").exists() ? (HashMap<String, Boolean>) dataSnapshot.child("localNetworks").getValue() : new HashMap<>();


        List<String> globalNetworks = new ArrayList<>();
        List<String> networkCountries = new ArrayList<>();
        Map<String, String> networkCountryNetworks = new HashMap<>();
        for (DataSnapshot network : dataSnapshot.child("networks").getChildren()) {
            globalNetworks.add(network.getKey());

            String networkCountryId = (String) network.child("networkCountryId").getValue();
            networkCountries.add(networkCountryId);
            networkCountryNetworks.put(network.getKey(), networkCountryId);
        }

        networkFetcherListener.onNetworkFetcherResult(new NetworkFetcherResult(new ArrayList<>(localNetworks.keySet()), globalNetworks, networkCountries, networkCountryNetworks));

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public interface NetworkFetcherListener{
        void onNetworkFetcherResult(NetworkFetcherResult networkFetcherResult);
    }

    public class NetworkFetcherResult{
        private List<String> localNetworks;
        private List<String> globalNetworks;
        private List<String> networksCountries;

        private Map<String, String> networkCountryNetworks;


        @Override
        public String toString() {
            return "NetworkFetcherResult{" +
                    "localNetworks=" + localNetworks +
                    ", globalNetworks=" + TextUtils.join(", " , globalNetworks) +
                    ", networksCountries=" + TextUtils.join(", ", networksCountries) +
                    '}';
        }

        public NetworkFetcherResult(List<String> localNetworks, List<String> globalNetworks, List<String> networksCountries, Map<String, String> networkCountryNetworks) {
            this.localNetworks = localNetworks;
            this.globalNetworks = globalNetworks;
            this.networksCountries = networksCountries;
            this.networkCountryNetworks = networkCountryNetworks;
        }

        public  List<String> all() {
            return AppUtils.smartCombine(AppUtils.smartCombine(localNetworks, globalNetworks), networksCountries);
        }

        public List<String> getLocalNetworks() {
            return localNetworks;
        }

        public void setLocalNetworks(List<String> localNetworks) {
            this.localNetworks = localNetworks;
        }

        public List<String> getGlobalNetworks() {
            return globalNetworks;
        }

        public void setGlobalNetworks(List<String> globalNetworks) {
            this.globalNetworks = globalNetworks;
        }

        public List<String> getNetworksCountries() {
            return networksCountries;
        }

        public void setNetworksCountries(List<String> networksCountries) {
            this.networksCountries = networksCountries;
        }

        public Map<String, String> getNetworkCountryNetworks() {
            return networkCountryNetworks;
        }

        public void setNetworkCountryNetworks(Map<String, String> networkCountryNetworks) {
            this.networkCountryNetworks = networkCountryNetworks;
        }
    }

}

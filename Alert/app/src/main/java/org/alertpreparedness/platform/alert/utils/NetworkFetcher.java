package org.alertpreparedness.platform.alert.utils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.CountryOfficeRef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.realm.internal.Util;

public class NetworkFetcher implements ValueEventListener {

    @Inject
    @CountryOfficeRef
    DatabaseReference countryOfficeRef;

    private NetworkFetcherListener networkFetcherListener;

    public NetworkFetcher(NetworkFetcherListener networkFetcherListener){
        this.networkFetcherListener = networkFetcherListener;
        DependencyInjector.applicationComponent().inject(this);
    }

    public void fetch(){
        countryOfficeRef.addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        System.out.println("NetworkFetcherdataSnapshot = " + dataSnapshot.getRef());
        //noinspection unchecked
        HashMap<String, Boolean> localNetworks = dataSnapshot.child("localNetworks").exists() ? (HashMap<String, Boolean>) dataSnapshot.child("localNetworks").getValue() : new HashMap<>();


        List<String> globalNetworks = new ArrayList<>();
        List<String> networkCountries = new ArrayList<>();
        for (DataSnapshot network : dataSnapshot.child("networks").getChildren()) {
            globalNetworks.add(network.getKey());

            networkCountries.add((String) network.child("networkCountryId").getValue());
        }

        networkFetcherListener.onNetworkFetcherResult(new NetworkFetcherResult(new ArrayList<>(localNetworks.keySet()), globalNetworks, networkCountries));

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

        @Override
        public String toString() {
            return "NetworkFetcherResult{" +
                    "localNetworks=" + localNetworks +
                    ", globalNetworks=" + globalNetworks +
                    ", networksCountries=" + networksCountries +
                    '}';
        }

        private List<String> networksCountries;

        public NetworkFetcherResult(List<String> localNetworks, List<String> globalNetworks, List<String> networksCountries) {
            this.localNetworks = localNetworks;
            this.globalNetworks = globalNetworks;
            this.networksCountries = networksCountries;
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
    }

}

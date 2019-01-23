package org.alertpreparedness.platform.v1.notifications;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseHazardRef;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseIndicatorRef;
import org.alertpreparedness.platform.v1.firebase.IndicatorModel;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.utils.SynchronizedCounter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class IndicatorFetcher implements SynchronizedCounter.SynchronizedCounterListener {

    private IndicatorFetcherListener listener;

    @Inject
    public User user;

    @Inject
    @BaseIndicatorRef
    public DatabaseReference baseIndicatorRef;

    @Inject
    @BaseHazardRef
    public DatabaseReference baseHazardRef;

    private boolean failed = false;

    private List<IndicatorFetcherResult> indicators = new ArrayList<>();
    private List<String> hazardIds = new ArrayList<>();
    private SynchronizedCounter hazardCounter;
    private SynchronizedCounter indicatorCounter;

    public IndicatorFetcher(IndicatorFetcherListener listener) {
        this.listener = listener;
        DependencyInjector.userScopeComponent().inject(this);
    }

    public void fetchAll(){

        hazardIds = new ArrayList<>();

        hazardIds.add(user.getCountryID());
        hazardIds.add(user.getNetworkCountryID());
        hazardIds.add(user.getLocalNetworkID());

        hazardCounter = new SynchronizedCounter(3);
        hazardCounter.addListener(this);

        baseHazardRef.child(user.getCountryID()).addListenerForSingleValueEvent(new HazardListener(hazardIds, hazardCounter));
        baseHazardRef.child(user.getNetworkCountryID()).addListenerForSingleValueEvent(new HazardListener(hazardIds, hazardCounter));
        baseHazardRef.child(user.getLocalNetworkID()).addListenerForSingleValueEvent(new HazardListener(hazardIds, hazardCounter));
    }

    @Override
    public void counterChanged(SynchronizedCounter synchronizedCounter, int amount) {
        if(synchronizedCounter == hazardCounter && amount == 0){
            fetchIndicators();
        }
        else if(synchronizedCounter == indicatorCounter && amount == 0){
            notifySuccess();
        }
    }

    private void notifySuccess() {
        listener.indicatorFetchSuccess(indicators);
    }

    private void fetchIndicators() {
        indicators = new ArrayList<>();
        indicatorCounter = new SynchronizedCounter(hazardIds.size());
        indicatorCounter.addListener(this);

        for (String hazardId : hazardIds) {
            baseIndicatorRef.child(hazardId).orderByChild("assignee").equalTo(user.getUserID()).addListenerForSingleValueEvent(new IndicatorListener(indicators, indicatorCounter, hazardId));
        }
    }

    public interface IndicatorFetcherListener{
        void indicatorFetchSuccess(List<IndicatorFetcherResult> models);
        void indicatorFetchFail();
    }

    private class HazardListener implements ValueEventListener {


        private final List<String> hazardIds;
        private final SynchronizedCounter hazardCounter;

        public HazardListener(List<String> hazardIds, SynchronizedCounter hazardCounter) {
            this.hazardIds = hazardIds;
            this.hazardCounter = hazardCounter;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Timber.d("Data Changed");
            if(dataSnapshot != null){
                for(DataSnapshot hazardSnap : dataSnapshot.getChildren()){
                    hazardIds.add(hazardSnap.getKey());
                }
            }
            hazardCounter.decrement();
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
            listener.indicatorFetchFail();
        }
    }

    private class IndicatorListener implements ValueEventListener {
        private final SynchronizedCounter indicatorCounter;
        private final List<IndicatorFetcherResult> indicators;
        private final String hazardId;

        public IndicatorListener(List<IndicatorFetcherResult> indicators, SynchronizedCounter indicatorCounter, String hazardId) {
            this.indicators = indicators;
            this.indicatorCounter = indicatorCounter;
            this.hazardId = hazardId;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for(DataSnapshot indicatorSnap : dataSnapshot.getChildren()){
                indicators.add(new IndicatorFetcherResult(indicatorSnap.getValue(IndicatorModel.class), indicatorSnap.getKey(), hazardId));
            }
            indicatorCounter.decrement();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            notifyFailed();
        }
    }


    public class IndicatorFetcherResult{
        private IndicatorModel indicator;
        private String indicatorId;
        private String hazardId;

        public IndicatorFetcherResult(IndicatorModel indicator, String indicatorId, String hazardId) {
            this.indicator = indicator;
            this.indicatorId = indicatorId;
            this.hazardId = hazardId;
        }

        public IndicatorModel getIndicator() {
            return indicator;
        }

        public String getHazardId() {
            return hazardId;
        }

        public String getIndicatorId() {
            return indicatorId;
        }
    }
}

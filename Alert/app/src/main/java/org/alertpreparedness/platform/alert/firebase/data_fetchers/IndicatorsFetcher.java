package org.alertpreparedness.platform.alert.firebase.data_fetchers;

import com.google.common.collect.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseIndicatorRef;
import org.alertpreparedness.platform.alert.dagger.annotation.HazardGroupObservable;
import org.alertpreparedness.platform.alert.dagger.annotation.HazardObservable;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.AppUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirebaseChildEvent;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.Flowable;
import timber.log.Timber;

public class IndicatorsFetcher implements FirebaseDataFetcher, RxFirebaseDataFetcher<DataSnapshot> {

    @Inject
    @BaseIndicatorRef
    DatabaseReference baseIndicatorRef;

    @Inject
    User user;


    @Inject
    Flowable<NetworkFetcher.NetworkFetcherResult> networkResultFlowable;

    @Inject
    @HazardGroupObservable
    Flowable<Collection<DataSnapshot>> hazardGroupFlowable;

    @Inject
    @HazardObservable
    Flowable<FetcherResultItem<DataSnapshot>> hazardFlowable;

    private IndicatorsFetcherListener indicatorsFetcherListener;

    @Deprecated
    public IndicatorsFetcher(IndicatorsFetcherListener indicatorsFetcherListener) {
        this.indicatorsFetcherListener = indicatorsFetcherListener;
        DependencyInjector.applicationComponent().inject(this);
    }

    public IndicatorsFetcher() {
        DependencyInjector.applicationComponent().inject(this);
    }


    //region FirebaseDataFetcher
    @Deprecated
    @Override
    public void fetch() {
        ValueEventListener indicatorsValueEventListener =
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot indicatorsSnapshot) {
                        for (DataSnapshot indicatorSnapshot : indicatorsSnapshot.getChildren()) {
                            indicatorsFetcherListener.onIndicatorsFetcherResult(indicatorSnapshot);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Timber.e("Error fetching hazards for network / country office: %s",
                                databaseError.getMessage());
                    }
                };

        new HazardsFetcher(hazardSnapshot -> {
            String hazardId = hazardSnapshot.getKey();
            baseIndicatorRef.child(hazardId).addValueEventListener(indicatorsValueEventListener);
        }).fetch();

        new NetworkFetcher(networkFetcherResult -> {
            for (String networkId : networkFetcherResult.all()) {
                baseIndicatorRef
                        .child(networkId)
                        .addValueEventListener(indicatorsValueEventListener);
            }
        }).fetch();

        String countryId = user.countryID;
        baseIndicatorRef
                .child(countryId)
                .addValueEventListener(indicatorsValueEventListener);
    }

    @Override
    public Flowable<FetcherResultItem<DataSnapshot>> rxFetch() {
        return networkResultFlowable.flatMap(networkFetcherResult -> {
            List<String> networkIds = networkFetcherResult.all();
            Flowable<RxFirebaseChildEvent<DataSnapshot>> flow = RxFirebaseDatabase.observeChildEvent(baseIndicatorRef.child(user.countryID));
            for (String networkId : networkIds) {
                flow = flow.mergeWith(RxFirebaseDatabase.observeChildEvent(baseIndicatorRef.child(networkId)));
            }
            return flow;
        }).mergeWith(hazardFlowable.flatMap(hazard -> RxFirebaseDatabase.observeChildEvent(baseIndicatorRef.child(hazard.getValue().getKey()))))
        .map(FetcherResultItem::new);
    }

    @Override
    public Flowable<Collection<DataSnapshot>> rxFetchGroup() {

        Flowable<Collection<DataSnapshot>> networkIndicatorsFlowable = networkResultFlowable.flatMap(networkFetcherResult -> {
            List<String> networkIds = networkFetcherResult.all();

            List<Flowable<List<DataSnapshot>>> flowables = new ArrayList<>();
            flowables.add(RxFirebaseDatabase.observeValueEvent(baseIndicatorRef.child(user.countryID)).map(dataSnapshot -> Lists.newArrayList(dataSnapshot.getChildren())));

            for (String networkId : networkIds) {
                flowables.add(RxFirebaseDatabase.observeValueEvent(baseIndicatorRef.child(networkId)).map(dataSnapshot -> Lists.newArrayList(dataSnapshot.getChildren())));
            }
            return Flowable.combineLatest(flowables, AppUtils::combineDataSnapshotList);
        });

        Flowable<Collection<DataSnapshot>> hazardIndicatorsFlowable = hazardGroupFlowable.flatMap(hazardGroup -> {
            List<Flowable<Collection<DataSnapshot>>> flowables = new ArrayList<>();
            for (DataSnapshot hazard : hazardGroup) {
                flowables.add(RxFirebaseDatabase.observeValueEvent(baseIndicatorRef.child(hazard.getKey())).map(dataSnapshot -> Lists.newArrayList(dataSnapshot.getChildren())));
            }
            if (flowables.size() != 0) {
                return Flowable.combineLatest(flowables, AppUtils::combineDataSnapshotList);
            } else {
                return Flowable.just(new ArrayList<>());
            }
        });

        List<Flowable<Collection<DataSnapshot>>> flowables = new ArrayList<>();
        flowables.add(networkIndicatorsFlowable);
        flowables.add(hazardIndicatorsFlowable);

        return Flowable.combineLatest(flowables, AppUtils::combineDataSnapshotList);
    }
    //endregion

    //region IndicatorsFetcherListener
    public interface IndicatorsFetcherListener {
        void onIndicatorsFetcherResult(DataSnapshot indicatorSnapshot);
    }
    //endregion
}

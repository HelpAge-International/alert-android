package org.alertpreparedness.platform.v1.firebase.data_fetchers;

import com.google.common.collect.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.BaseHazardRef;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.utils.AppUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirebaseChildEvent;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.Flowable;
import timber.log.Timber;

/**
 * Fetches all hazard IDs that should be visible to the current user.
 */
public class HazardsFetcher implements FirebaseDataFetcher, RxFirebaseDataFetcher<DataSnapshot> {

    @Inject
    @BaseHazardRef
    DatabaseReference baseHazardRef;

    @Inject
    User user;

    private HazardsFetcherListener hazardsFetcherListener;

    @Inject
    Flowable<NetworkFetcher.NetworkFetcherResult> networkResultFlowable;

    @Deprecated
    public HazardsFetcher(HazardsFetcherListener hazardsFetcherListener) {
        this.hazardsFetcherListener = hazardsFetcherListener;
        DependencyInjector.userScopeComponent().inject(this);
    }

    public HazardsFetcher() {
        DependencyInjector.userScopeComponent().inject(this);
    }

    //region FirebaseDataFetcher
    @Deprecated
    @Override
    public void fetch() {
        // Fetching the network IDs IDs
        new NetworkFetcher(networkFetcherResult -> {

            // Parsing hazard data snapshots
            ValueEventListener hazardsValueListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot hazardsSnapshot) {
                    for (DataSnapshot hazardWithIdSnapshot : hazardsSnapshot.getChildren()) {
                        hazardsFetcherListener.onHazardsFetcherResult(hazardWithIdSnapshot);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Timber.e("Error fetching hazards for network / country office: %s",
                            databaseError.getMessage());
                }
            };

            // Fetching all hazards for networks the the user is part of
            for (String networkId : networkFetcherResult.all()) {
                baseHazardRef
                        .child(networkId)
                        .addValueEventListener(hazardsValueListener);
            }

            // Fetching hazards for the country office
            baseHazardRef
                    .child(user.countryID)
                    .addValueEventListener(hazardsValueListener);
        }).fetch();
    }

    @Override
    public Flowable<FetcherResultItem<DataSnapshot>> rxFetch() {
        return networkResultFlowable.flatMap(networkFetcherResult -> {
            List<String> networkIds = networkFetcherResult.all();
            Flowable<RxFirebaseChildEvent<DataSnapshot>> flow = RxFirebaseDatabase.observeChildEvent(baseHazardRef.child(user.countryID));
            for (String networkId : networkIds) {
                flow = flow.mergeWith(RxFirebaseDatabase.observeChildEvent(baseHazardRef.child(networkId)));
            }
            return flow.map(FetcherResultItem::new);
        });
    }

    @Override
    public Flowable<Collection<DataSnapshot>> rxFetchGroup() {
        return networkResultFlowable.flatMap(networkFetcherResult -> {
            List<String> networkIds = networkFetcherResult.all();

            List<Flowable<List<DataSnapshot>>> flowables = new ArrayList<>();
            flowables.add(RxFirebaseDatabase.observeValueEvent(baseHazardRef.child(user.countryID)).map(dataSnapshot -> Lists.newArrayList(dataSnapshot.getChildren())));

            for (String networkId : networkIds) {
                flowables.add(RxFirebaseDatabase.observeValueEvent(baseHazardRef.child(networkId)).map(dataSnapshot -> Lists.newArrayList(dataSnapshot.getChildren())));
            }
            return Flowable.combineLatest(flowables, AppUtils::combineDataSnapshotList);
        });
    }
    //endregion

    //region HazardsFetcherListener
    public interface HazardsFetcherListener {
        void onHazardsFetcherResult(DataSnapshot hazardSnapshot);
    }
    //endregion
}

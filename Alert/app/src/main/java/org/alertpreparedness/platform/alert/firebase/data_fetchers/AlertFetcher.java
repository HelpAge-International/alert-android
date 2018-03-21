package org.alertpreparedness.platform.alert.firebase.data_fetchers;

import com.google.common.collect.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseAlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.NetworkRef;
import org.alertpreparedness.platform.alert.firebase.wrappers.AlertResultWrapper;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.AppUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirebaseChildEvent;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.Flowable;

/**
 * Created by Tj on 01/03/2018.
 */

public class AlertFetcher implements RxFirebaseDataFetcher<DataSnapshot> {

    @Inject
    User user;

    @Inject
    @BaseAlertRef
    DatabaseReference alertRef;

    @Inject
    @NetworkRef
    DatabaseReference networkRef;

    @Inject
    Flowable<NetworkFetcher.NetworkFetcherResult> networkResultFlowable;


    public AlertFetcher(){
        DependencyInjector.userScopeComponent().inject(this);
    }

    @Override
    public Flowable<FetcherResultItem<DataSnapshot>> rxFetch(){
        return networkResultFlowable.flatMap(networkFetcherResult -> {
            List<String> networkIds = networkFetcherResult.all();
            Flowable<RxFirebaseChildEvent<DataSnapshot>> flow = RxFirebaseDatabase.observeChildEvent(alertRef.child(user.countryID));
            for (String networkId : networkIds) {
                flow = flow.mergeWith(RxFirebaseDatabase.observeChildEvent(alertRef.child(networkId)));
            }
            return flow.map(FetcherResultItem::new);
        });
    }

    public Flowable<FetcherResultItem<AlertResultWrapper>> fetchWithExtra() {
        return networkResultFlowable.flatMap(networkFetcherResult -> {
            List<String> networkIds = networkFetcherResult.all();
            Flowable<NetworkResult> flow = RxFirebaseDatabase.observeValueEvent(alertRef.child(user.countryID))
                    .map(dataSnapshot -> new NetworkResult(false, null, user.countryID));
            for (String networkId : networkIds) {
                flow = flow.mergeWith(RxFirebaseDatabase.observeValueEvent(networkRef.child(networkId))
                        .map(dataSnapshot ->
                                new NetworkResult(true, dataSnapshot.child("leadAgencyId").getValue(String.class), networkId)
                        )
                );
            }
            return flow;
        }).flatMap(networkResult -> {
            Flowable<RxFirebaseChildEvent<DataSnapshot>> flowable = RxFirebaseDatabase.observeChildEvent(alertRef.child(networkResult.getParentId()));
            return flowable.map(snapshotRxFirebaseChildEvent -> new FetcherResultItem<>(
                    new AlertResultWrapper(
                            networkResult.parentId,
                            networkResult.isNetwork,
                            networkResult.networkLeadId,
                            snapshotRxFirebaseChildEvent.getValue()
                    ),
                    snapshotRxFirebaseChildEvent.getEventType()
            ));
        });
    }

    @Override
    public Flowable<Collection<DataSnapshot>> rxFetchGroup() {
        return networkResultFlowable.flatMap(networkFetcherResult -> {
            List<String> networkIds = networkFetcherResult.all();

            List<Flowable<List<DataSnapshot>>> flowables = new ArrayList<>();
            flowables.add(RxFirebaseDatabase.observeValueEvent(alertRef.child(user.countryID)).map(dataSnapshot -> Lists.newArrayList(dataSnapshot.getChildren())));

            for (String networkId : networkIds) {
                flowables.add(RxFirebaseDatabase.observeValueEvent(alertRef.child(networkId)).map(dataSnapshot -> Lists.newArrayList(dataSnapshot.getChildren())));
            }
            return Flowable.combineLatest(flowables, AppUtils::combineDataSnapshotList);
        });
    }

    class NetworkResult {
        private final boolean isNetwork;
        private final String networkLeadId;
        private String parentId;

        public NetworkResult(boolean isNetwork, String networkLeadId, String parentId) {
            this.isNetwork = isNetwork;
            this.networkLeadId = networkLeadId;
            this.parentId = parentId;
        }

        public boolean isNetwork() {
            return isNetwork;
        }

        public String getNetworkLeadId() {
            return networkLeadId;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }
    }

}

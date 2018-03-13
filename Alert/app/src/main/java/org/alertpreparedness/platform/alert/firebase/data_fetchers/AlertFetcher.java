package org.alertpreparedness.platform.alert.firebase.data_fetchers;

import com.google.common.collect.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseAlertRef;
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
    Flowable<NetworkFetcher.NetworkFetcherResult> networkResultFlowable;


    public AlertFetcher(){
        DependencyInjector.applicationComponent().inject(this);
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


}

package org.alertpreparedness.platform.alert.firebase.data_fetchers;

import android.util.Pair;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionCHSRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.firebase.wrappers.ActionItemWrapper;
import org.alertpreparedness.platform.alert.model.User;

import java.util.List;

import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirebaseChildEvent;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.Flowable;

/**
 * Created by Tj on 08/03/2018.
 */

public class TempActionFetcher implements RxFirebaseDataFetcher<ActionItemWrapper> {

    @Inject
    User user;

    @Inject
    @BaseActionRef
    DatabaseReference dbActionBaseRef;

    @Inject
    @BaseActionCHSRef
    DatabaseReference dbBaseActionCHSRef;

    @Inject
    Flowable<NetworkFetcher.NetworkFetcherResult> networkResultFlowable;

    public TempActionFetcher() {
        DependencyInjector.applicationComponent().inject(this);
    }

    @Override
    public Flowable<RxFirebaseChildEvent<ActionItemWrapper>> rxFetch() {
        System.out.println("TempActionFetcher.rxFetch");
        Flowable<RxFirebaseChildEvent<DataSnapshot>> a = networkResultFlowable.flatMap(networkFetcherResult -> {
            List<String> networkIds = networkFetcherResult.all();
            Flowable<RxFirebaseChildEvent<DataSnapshot>> flow = RxFirebaseDatabase.observeChildEvent(dbActionBaseRef.child(user.countryID).orderByChild("type").equalTo(2));
            for (String networkId : networkIds) {
                flow = flow.mergeWith(RxFirebaseDatabase.observeChildEvent(dbActionBaseRef.child(networkId).orderByChild("type").equalTo(2)));
            }
            return flow;
        });


        Flowable<RxFirebaseChildEvent<ActionItemWrapper>> chsFlowable = networkResultFlowable.flatMap(networkFetcherResult -> {
            List<String> networkIds = networkFetcherResult.all();
            Flowable<RxFirebaseChildEvent<DataSnapshot>> flow = RxFirebaseDatabase.observeChildEvent(dbBaseActionCHSRef.child(user.countryID));
            for (String networkId : networkIds) {
                flow = flow.mergeWith(RxFirebaseDatabase.observeChildEvent(dbBaseActionCHSRef.child(networkId)));
            }
            return flow;
        }).flatMap(chsAction -> {
            return RxFirebaseDatabase.observeChildEvent(dbActionBaseRef.child(chsAction.getValue().getRef().getParent().getKey()).child(chsAction.getValue().getKey()))
                    .map(action -> new Pair<>(chsAction, action));
        }).map(snapshotRxFirebaseChildEvent -> {
            if(snapshotRxFirebaseChildEvent.second.getValue().exists()) {
                return ActionItemWrapper.createCHS(snapshotRxFirebaseChildEvent.first.getValue(), snapshotRxFirebaseChildEvent.second.getValue());;
            }
            else {
                return ActionItemWrapper.createCHS(snapshotRxFirebaseChildEvent.first.getValue());
            }
        });



        return a;
    }

    @Override
    public Flowable<List<ActionItemWrapper>> rxFetchGroup() {
        return null;
    }
}

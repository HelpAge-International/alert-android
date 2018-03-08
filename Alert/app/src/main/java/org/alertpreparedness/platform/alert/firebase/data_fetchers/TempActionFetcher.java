package org.alertpreparedness.platform.alert.firebase.data_fetchers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionCHSRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionCHSRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.model.User;
import org.intellij.lang.annotations.Flow;

import java.util.List;

import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirebaseChildEvent;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

/**
 * Created by Tj on 08/03/2018.
 */

public class TempActionFetcher implements RxFirebaseDataFetcher {

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
    public Flowable<RxFirebaseChildEvent<DataSnapshot>> rxFetch() {
        System.out.println("TempActionFetcher.rxFetch");
        Flowable<RxFirebaseChildEvent<DataSnapshot>> a = networkResultFlowable.flatMap(networkFetcherResult -> {
            List<String> networkIds = networkFetcherResult.all();
            Flowable<RxFirebaseChildEvent<DataSnapshot>> flow = RxFirebaseDatabase.observeChildEvent(dbActionBaseRef.child(user.countryID).orderByChild("type").equalTo(2));
            for (String networkId : networkIds) {
                flow = flow.mergeWith(RxFirebaseDatabase.observeChildEvent(dbActionBaseRef.child(networkId).orderByChild("type").equalTo(2)));
            }
            return flow;
        });




        Flowable<RxFirebaseChildEvent<DataSnapshot>> b = networkResultFlowable.flatMap(networkFetcherResult -> {
            List<String> networkIds = networkFetcherResult.all();
            Flowable<RxFirebaseChildEvent<DataSnapshot>> flow = RxFirebaseDatabase.observeChildEvent(dbBaseActionCHSRef.child(user.countryID));
            for (String networkId : networkIds) {
                flow = flow.mergeWith(RxFirebaseDatabase.observeChildEvent(dbBaseActionCHSRef.child(networkId)));
            }
            return flow;
        });

//        a.combi

//        Flowable.create(emitter -> {
//
//            networkResultFlowable.flatMap(networkFetcherResult -> {
//                List<String> networkIds = networkFetcherResult.all();
//                Flowable<RxFirebaseChildEvent<DataSnapshot>> flow = RxFirebaseDatabase.observeChildEvent(dbBaseActionCHSRef.child(user.countryID));
//                for (String networkId : networkIds) {
//                    flow = flow.mergeWith(RxFirebaseDatabase.observeChildEvent(dbBaseActionCHSRef.child(networkId)));
//                }
//            });
//
//        }, BackpressureStrategy.BUFFER);
        return a;
    }

    @Override
    public Flowable<List<DataSnapshot>> rxFetchGroup() {
        return null;
    }
}

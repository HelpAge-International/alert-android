package org.alertpreparedness.platform.alert.firebase.data_fetchers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseAlertRef;
import org.alertpreparedness.platform.alert.model.User;

import java.util.List;

import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirebaseChildEvent;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.Flowable;

/**
 * Created by Tj on 01/03/2018.
 */

public class AlertFetcher implements RxFirebaseDataFetcher {

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
    public Flowable<RxFirebaseChildEvent<DataSnapshot>> rxFetch(){
        return networkResultFlowable.flatMap(networkFetcherResult -> {
            List<String> networkIds = networkFetcherResult.all();
            Flowable<RxFirebaseChildEvent<DataSnapshot>> flow = RxFirebaseDatabase.observeChildEvent(alertRef.child(user.countryID));
            for (String networkId : networkIds) {
                flow = flow.mergeWith(RxFirebaseDatabase.observeChildEvent(alertRef.child(networkId)));
            }
            return flow;
        });
    }

    @Override
    public Flowable<List<DataSnapshot>> rxFetchGroup() {
        return null;
    }


}

package org.alertpreparedness.platform.alert.firebase.data_fetchers;

import com.google.firebase.database.DataSnapshot;

import java.util.List;

import durdinapps.rxfirebase2.RxFirebaseChildEvent;
import io.reactivex.Flowable;

/**
 * Created by Tj on 08/03/2018.
 */

public interface RxFirebaseDataFetcher {
    public Flowable<RxFirebaseChildEvent<DataSnapshot>> rxFetch();
    public Flowable<List<DataSnapshot>> rxFetchGroup();
}

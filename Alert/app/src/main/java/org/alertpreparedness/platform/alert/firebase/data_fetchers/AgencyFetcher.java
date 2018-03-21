package org.alertpreparedness.platform.alert.firebase.data_fetchers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;

import java.util.Collection;

import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.Flowable;

/**
 * Created by Tj on 13/03/2018.
 */

public class AgencyFetcher implements RxFirebaseDataFetcher<DataSnapshot> {

    @Inject
    @AgencyRef
    DatabaseReference agencyRef;

    public AgencyFetcher() {
        DependencyInjector.userScopeComponent().inject(this);
    }

    @Override
    public Flowable<FetcherResultItem<DataSnapshot>> rxFetch() {
        return RxFirebaseDatabase.observeSingleValueEvent(agencyRef).map(FetcherResultItem::new).toFlowable();
    }

    @Override
    public Flowable<Collection<DataSnapshot>> rxFetchGroup() {
        return null;
    }
}

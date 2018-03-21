package org.alertpreparedness.platform.alert.firebase.data_fetchers;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseNoteRef;
import org.alertpreparedness.platform.alert.dagger.annotation.ResponsePlansRef;
import org.alertpreparedness.platform.alert.firebase.wrappers.ResponsePlanResultItem;

import java.util.Collection;

import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirebaseChildEvent;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.Flowable;

    /**
     * Created by Tj on 13/03/2018.
     */

    public class ResponsePlanFetcher implements RxFirebaseDataFetcher<ResponsePlanResultItem> {

        @Inject
        @ResponsePlansRef
        DatabaseReference responsePlans;

        @Inject
        @BaseNoteRef
        DatabaseReference noteRef;

        public ResponsePlanFetcher() {
            DependencyInjector.userScopeComponent().inject(this);
        }

        @Override
        public Flowable<FetcherResultItem<ResponsePlanResultItem>> rxFetch() {
            Flowable<RxFirebaseChildEvent<DataSnapshot>> flow = RxFirebaseDatabase.observeChildEvent(responsePlans);

            return flow.map(snapshotRxFirebaseChildEvent ->
                    new FetcherResultItem<>(new ResponsePlanResultItem(snapshotRxFirebaseChildEvent.getValue()), snapshotRxFirebaseChildEvent.getEventType()));
        }

        @Override
        public Flowable<Collection<ResponsePlanResultItem>> rxFetchGroup() {
            return RxFirebaseDatabase.observeValueEvent(responsePlans)
                    .map(dataSnapshot -> Collections2.transform(Lists.newArrayList(dataSnapshot.getChildren()), ResponsePlanResultItem::new));
        }


}

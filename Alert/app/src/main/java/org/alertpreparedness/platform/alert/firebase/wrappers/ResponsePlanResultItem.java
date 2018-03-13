package org.alertpreparedness.platform.alert.firebase.wrappers;

import android.util.Pair;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseNoteRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.FetcherResultItem;

import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirebaseChildEvent;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by Tj on 13/03/2018.
 */

public class ResponsePlanResultItem {

    private DataSnapshot responsePlan;

    @Inject
    @BaseNoteRef
    DatabaseReference noteRef;

    @Inject
    @UserPublicRef
    DatabaseReference userRef;

    public ResponsePlanResultItem(DataSnapshot responsePlan) {
        this.responsePlan = responsePlan;
        DependencyInjector.applicationComponent().inject(this);
    }

    public Flowable<FetcherResultItem<NoteItemWrapper>> getNotes() {

        Flowable<RxFirebaseChildEvent<DataSnapshot>> snapshotFlowable =  RxFirebaseDatabase.observeChildEvent(noteRef.child(responsePlan.getKey()));

        return snapshotFlowable.map(snapshot -> {
            String userKey = snapshot.getValue().child("uploadBy").getValue(String.class);
            assert userKey != null;
            return new Pair<>(RxFirebaseDatabase.observeSingleValueEvent(userRef.child(userKey)).toSingle(), snapshot);
        }).map(pair -> new FetcherResultItem<>(new NoteItemWrapper(pair.second.getValue(), pair.first), pair.second.getEventType()));

    }

    public DataSnapshot getResponsePlan() {
        return responsePlan;
    }

    public void setResponsePlan(DataSnapshot responsePlan) {
        this.responsePlan = responsePlan;
    }
}

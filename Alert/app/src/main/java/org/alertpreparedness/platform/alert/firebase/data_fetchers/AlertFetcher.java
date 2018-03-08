package org.alertpreparedness.platform.alert.firebase.data_fetchers;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import org.alertpreparedness.platform.alert.action.ActionFetcher;
import org.alertpreparedness.platform.alert.action.IdFetcherListener;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseAlertRef;
import org.alertpreparedness.platform.alert.firebase.AlertModel;
import org.alertpreparedness.platform.alert.firebase.data_fetchers.NetworkFetcher;
import org.alertpreparedness.platform.alert.model.User;

import java.io.StringReader;
import java.util.List;
import java.util.concurrent.Future;

import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirebaseChildEvent;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

/**
 * Created by Tj on 01/03/2018.
 */

public class AlertFetcher implements RxFirebaseDataFetcher {

    private AlertFetcherListener listener;

    @Inject
    User user;

    @Inject
    @BaseAlertRef
    DatabaseReference alertRef;

    @Inject
    Flowable<NetworkFetcher.NetworkFetcherResult> networkResultFlowable;

    public AlertFetcher(AlertFetcherListener listener) {
        this.listener = listener;
        DependencyInjector.applicationComponent().inject(this);
    }

    public AlertFetcher(){
        DependencyInjector.applicationComponent().inject(this);
    }

    public void fetchWithIds(List<String> networkIds, IdFetcherListener idFetcherListener) {
        networkIds.add(user.countryID);
        idFetcherListener.onIdResult(networkIds);
        for (String id : networkIds) {
            alertRef.child(id).addChildEventListener(new AlertListener(id));
        }
    }

    public void fetch(IdFetcherListener idFetcherListener) {
        new NetworkFetcher((n) -> {
            List<String> networkIds = n.all();
            networkIds.add(user.countryID);
            idFetcherListener.onIdResult(networkIds);
            for (String id : networkIds) {
                alertRef.child(id).addChildEventListener(new AlertListener(id));
            }
        }).fetch();
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

    public interface AlertFetcherListener {
        void onAlertRetrieved(DataSnapshot snapshot, AlertModel model);
        void onAlertRemoved(DataSnapshot snapshot);
    }

    private class AlertListener implements ChildEventListener {
        private String parentId;

        public AlertListener(String parentId) {
            this.parentId = parentId;
        }

        private void process(DataSnapshot dataSnapshot) {

            final GsonBuilder gsonBuilder = new GsonBuilder();
            final Gson gson = gsonBuilder.create();

            JsonReader reader = new JsonReader(new StringReader(gson.toJson(dataSnapshot.getValue()).trim()));
            reader.setLenient(true);
            AlertModel model = gson.fromJson(reader, AlertModel.class);

            assert model != null;
            model.setKey(dataSnapshot.getKey());
            model.setParentKey(parentId);

            listener.onAlertRetrieved(dataSnapshot, model);

        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            process(dataSnapshot);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            process(dataSnapshot);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            listener.onAlertRemoved(dataSnapshot);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}

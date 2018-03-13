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

public class FetcherResultItem<T> {

    private T value;
    private EventType eventType;

    public enum EventType{
        UPDATED,
        REMOVED
    }

    public FetcherResultItem(T value, EventType eventType) {
        this.value = value;
        this.eventType = eventType;
    }

    public FetcherResultItem(T value, RxFirebaseChildEvent.EventType eventType){
        this.value = value;
        if(eventType == RxFirebaseChildEvent.EventType.REMOVED){
            this.eventType = EventType.REMOVED;
        }
        else{
            this.eventType = EventType.UPDATED;
        }
    }


    public FetcherResultItem(RxFirebaseChildEvent<T> childEvent){
        this.value = childEvent.getValue();
        if(childEvent.getEventType() == RxFirebaseChildEvent.EventType.REMOVED){
            this.eventType = EventType.REMOVED;
        }
        else{
            this.eventType = EventType.UPDATED;
        }
    }

    public T getValue() {
        return value;
    }

    public EventType getEventType() {
        return eventType;
    }
}

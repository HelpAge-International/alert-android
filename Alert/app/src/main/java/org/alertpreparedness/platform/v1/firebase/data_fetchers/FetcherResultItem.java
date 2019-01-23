package org.alertpreparedness.platform.v1.firebase.data_fetchers;

import durdinapps.rxfirebase2.RxFirebaseChildEvent;

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

    public FetcherResultItem(T value){
        this.value = value;
        this.eventType = EventType.UPDATED;
    }

    public T getValue() {
        return value;
    }

    public EventType getEventType() {
        return eventType;
    }
}

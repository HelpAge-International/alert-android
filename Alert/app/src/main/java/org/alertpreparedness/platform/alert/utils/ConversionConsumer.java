package org.alertpreparedness.platform.alert.utils;

import com.google.firebase.database.DataSnapshot;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import durdinapps.rxfirebase2.RxFirebaseChildEvent;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.functions.Consumer;

public class ConversionConsumer<T> extends FirebaseConsumer<T> {
    private final Class<T> type;

    public ConversionConsumer(Class<T> type, FirebaseConsumer.OnChangedRemovedListener<T> onChangedRemovedListener) {
        super(onChangedRemovedListener);
        this.type = type;
    }

    public ConversionConsumer(Class<T> type, FirebaseConsumer.OnChangedListener<T> onChangedListener) {
        super(onChangedListener);
        this.type = type;
    }

    public ConversionConsumer(Class<T> type, FirebaseConsumer.OnRemovedListener<T> onRemovedListener) {
        super(onRemovedListener);
        this.type = type;
    }

    public ConversionConsumer(Class<T> type, FirebaseConsumer.OnChangedListener<T> onChangedListener, FirebaseConsumer.OnRemovedListener<T> onRemovedListener) {
        super(onChangedListener, onRemovedListener);
        this.type = type;
    }

    @Override
    public void accept(RxFirebaseChildEvent<DataSnapshot> tRxFirebaseChildEvent) throws Exception {
        switch (tRxFirebaseChildEvent.getEventType()){
            case ADDED:
                onChanged(AppUtils.getValueFromDataSnapshot(tRxFirebaseChildEvent.getValue(), type));
                break;
            case CHANGED:
                onChanged(AppUtils.getValueFromDataSnapshot(tRxFirebaseChildEvent.getValue(), type));
                break;
            case REMOVED:
                onRemoved(AppUtils.getValueFromDataSnapshot(tRxFirebaseChildEvent.getValue(), type));
                break;
            case MOVED:
                break;
        }

    }
}

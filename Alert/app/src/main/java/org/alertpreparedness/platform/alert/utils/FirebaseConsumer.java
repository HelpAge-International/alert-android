package org.alertpreparedness.platform.alert.utils;

import com.google.firebase.database.DataSnapshot;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import durdinapps.rxfirebase2.RxFirebaseChildEvent;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.functions.Consumer;

public class FirebaseConsumer<T> implements Consumer<RxFirebaseChildEvent<DataSnapshot>> {

    private OnChangedListener<T> onChangedListener;
    private OnRemovedListener<T> onRemovedListener;

    private final Class<T> type;

    public FirebaseConsumer(Class<T> type, OnChangedRemovedListener<T> onChangedRemovedListener) {
        this.type = type;
        this.onChangedListener = onChangedRemovedListener;
        this.onRemovedListener = onChangedRemovedListener;
    }

    public FirebaseConsumer(Class<T> type, OnChangedListener<T> onChangedListener) {
        this.type = type;
        this.onChangedListener = onChangedListener;
    }

    public FirebaseConsumer(Class<T> type, OnRemovedListener<T> onRemovedListener) {
        this.type = type;
        this.onRemovedListener = onRemovedListener;
    }

    public FirebaseConsumer(Class<T> type, OnChangedListener<T> onChangedListener, OnRemovedListener<T> onRemovedListener) {
        this.type = type;
        this.onChangedListener = onChangedListener;
        this.onRemovedListener = onRemovedListener;
    }


    public void onChanged(T t){
        if(onChangedListener != null){
            onChangedListener.onChanged(t);
        }
    }

    public void onRemoved(T t){
        if(onRemovedListener != null){
            onRemovedListener.onRemoved(t);
        }
    }

    @Override
    public void accept(RxFirebaseChildEvent<DataSnapshot> tRxFirebaseChildEvent) throws Exception {
        System.out.println("FirebaseConsumer.onNext");
        System.out.println("tRxFirebaseChildEvent = [" + tRxFirebaseChildEvent + "]");
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


    public interface OnChangedListener<T>{
        void onChanged(T t);
    }
    public interface OnRemovedListener<T>{
        void onRemoved(T t);
    }

    public interface OnChangedRemovedListener<T> extends OnChangedListener<T>, OnRemovedListener<T>{}

}

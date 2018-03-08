package org.alertpreparedness.platform.alert.utils;

import com.google.firebase.database.DataSnapshot;

import durdinapps.rxfirebase2.RxFirebaseChildEvent;
import io.reactivex.functions.Consumer;

public abstract class FirebaseConsumer<T> implements Consumer<RxFirebaseChildEvent<DataSnapshot>> {

    private OnChangedListener<T> onChangedListener;
    private OnRemovedListener<T> onRemovedListener;


    public FirebaseConsumer(OnChangedRemovedListener<T> onChangedRemovedListener) {
        this.onChangedListener = onChangedRemovedListener;
        this.onRemovedListener = onChangedRemovedListener;
    }

    public FirebaseConsumer(OnChangedListener<T> onChangedListener) {
        this.onChangedListener = onChangedListener;
    }

    public FirebaseConsumer(OnRemovedListener<T> onRemovedListener) {
        this.onRemovedListener = onRemovedListener;
    }

    public FirebaseConsumer(OnChangedListener<T> onChangedListener, OnRemovedListener<T> onRemovedListener) {
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

    public interface OnChangedListener<T>{
        void onChanged(T t);
    }
    public interface OnRemovedListener<T>{
        void onRemoved(T t);
    }

    public interface OnChangedRemovedListener<T> extends OnChangedListener<T>, OnRemovedListener<T>{}

}

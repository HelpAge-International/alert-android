package org.alertpreparedness.platform.alert.utils;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by Tj on 14/03/2018.
 */

public class ObservableSet<T> {

    private ArrayList<T> list = new ArrayList<>();

    private ArrayList<ObservableListItemAdded<T>> addListeners = new ArrayList<>();

    private ArrayList<ObservableListItemRemoved<T>> removeListeners = new ArrayList<>();

    public void observe(ObservableListItemAdded<T> l1, ObservableListItemRemoved<T> l2) {
        addListeners.add(l1);
        removeListeners.add(l2);
    }

    public void addItem(T item) {
        if(list.indexOf(item) == -1) {
            list.add(item);
            notifyObservers(true, item);
        }
    }

    public void removeItem(T item) {
        list.remove(item);
        notifyObservers(false, item);
    }

    private void notifyObservers(boolean added, T item) {
        if(added) {
            for (ObservableListItemAdded<T> listener : addListeners) {
                listener.onItemAdded(item);
            }
        }
        else {
            for (ObservableListItemRemoved<T> listener : removeListeners) {
                listener.onItemRemoved(item);
            }
        }
    }

    public interface ObservableListItemAdded<T> {
        void onItemAdded(T item);
    }

    public interface ObservableListItemRemoved<T> {
        void onItemRemoved(T item);
    }
}

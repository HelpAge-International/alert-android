package org.alertpreparedness.platform.alert.utils;

import java.util.ArrayList;
import java.util.List;

public class SynchronizedCounter {
    private int c;

    private List<SynchronizedCounterListener> listeners = new ArrayList<>();

    public SynchronizedCounter(int c) {
        this.c = c;
    }

    public synchronized void increment() {
        c++;
        notifyListeners();
    }

    public synchronized void decrement() {
        c--;
        notifyListeners();
    }

    private void notifyListeners() {
        for (SynchronizedCounterListener listener : listeners) {
            listener.counterChanged(this, c);
        }
    }

    public synchronized int value() {
        return c;
    }

    public void addListener(SynchronizedCounterListener synchronizedCounterListener){
        listeners.add(synchronizedCounterListener);
        synchronizedCounterListener.counterChanged(this, c);
    }

    public interface SynchronizedCounterListener {
        void counterChanged(SynchronizedCounter synchronizedCounter, int amount);
    }
}

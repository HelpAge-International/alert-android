package org.alertpreparedness.platform.alert.utils;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Jordan Fisher
 * Dated: 21/11/2016
 * Email: jordan@rolleragency.co.uk
 * <<--------------------->>
 * Copyright Roller Agency
 */

public class DBListener {

    List<DBList> listeners;

    public DBListener() {
        listeners = new ArrayList<>();
    }

    public void add(DatabaseReference ref, ChildEventListener listener) {
        listeners.add(new DBList(ref, listener));
    }
    public void add(Query ref, ChildEventListener listener) {
        listeners.add(new DBList(ref, listener));
    }
    public void add(DatabaseReference ref, ValueEventListener listener) {
        listeners.add(new DBList(ref, listener));
    }

    public void attach() {
        for (DBList list : listeners) {
            list.attach();
        }
    }

    public void detatch() {
        for (DBList list : listeners) {
            list.detatch();
        }
    }

    public static DBListener create() {
        return new DBListener();
    }

    public class DBList {

        private boolean isChild;
        private DatabaseReference mRef;
        private ChildEventListener mChildListener;
        private ValueEventListener mValueListener;
        private Query mQuery;

        public void attach() {
            if (isChild) {
                mRef.addChildEventListener(mChildListener);
            } else {
                mRef.addValueEventListener(mValueListener);
            }
        }

        public void detatch() {
            if (isChild) {
                if (mRef != null) {
                    mRef.removeEventListener(mChildListener);
                }
                if (mQuery != null) {
                    mQuery.removeEventListener(mChildListener);
                }
            } else {
                mRef.removeEventListener(mValueListener);
            }
        }

        public DBList(DatabaseReference ref, ChildEventListener listener) {
            this.mRef = ref;
            this.mChildListener = listener;
            this.isChild = true;
        }
        public DBList(DatabaseReference ref, ValueEventListener listener) {
            this.mRef = ref;
            this.mValueListener = listener;
            this.isChild = false;
        }

        public DBList(Query query, ChildEventListener listener) {
            this.mQuery = query;
            this.mChildListener = listener;
            this.isChild = true;
        }
    }
}
package org.alertpreparedness.platform.v1.utils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import org.alertpreparedness.platform.v1.model.User;

/**
 * Created by Tj on 27/02/2018.
 */

public class SnapshotExclusionStrat implements ExclusionStrategy {

    @Override
    public boolean shouldSkipClass(Class<?> arg0) {
        return false;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return (f.getDeclaredType() == DataSnapshot.class
                || f.getDeclaredType() == DatabaseReference.class
                || f.getDeclaredType() == User.class
        );
    }


}

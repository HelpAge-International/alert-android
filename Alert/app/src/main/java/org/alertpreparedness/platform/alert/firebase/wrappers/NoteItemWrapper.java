package org.alertpreparedness.platform.alert.firebase.wrappers;

import com.google.firebase.database.DataSnapshot;

import org.alertpreparedness.platform.alert.adv_preparedness.model.UserModel;

import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by Tj on 13/03/2018.
 */

public class NoteItemWrapper {
    private final DataSnapshot value;
    private final Single<DataSnapshot> user;

    public NoteItemWrapper(DataSnapshot value, Single<DataSnapshot> user) {
        this.value = value;
        this.user = user;
    }

    public DataSnapshot getValue() {
        return value;
    }

    public Single<DataSnapshot> getUser() {
        return user;
    }
}

package org.alertpreparedness.platform.alert.action;

import android.service.autofill.Dataset;

import com.google.firebase.database.DataSnapshot;

import org.alertpreparedness.platform.alert.min_preparedness.model.Action;

/**
 * Created by Tj on 28/02/2018.
 */

public interface ActionProcessorListener {

    void onAddAction(DataSnapshot snapshot, Action action);

    void tryRemoveAction(DataSnapshot snapshot);

}

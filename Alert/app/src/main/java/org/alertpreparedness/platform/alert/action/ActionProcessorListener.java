package org.alertpreparedness.platform.alert.action;

import org.alertpreparedness.platform.alert.min_preparedness.model.Action;

/**
 * Created by Tj on 28/02/2018.
 */

interface ActionProcessorListener {

    void onAddAction(String key, Action action);

    void tryRemoveAction(String key);

}

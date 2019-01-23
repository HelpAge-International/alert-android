package org.alertpreparedness.platform.v1.min_preparedness.adapter;

import org.alertpreparedness.platform.v1.firebase.ActionModel;

/**
 * Created by Tj on 30/01/2018.
 */

public interface PreparednessAdapter {
    public void addItems(String key, ActionModel action);
    public void removeItem(String key);
    ActionModel getItem(int pos);
}

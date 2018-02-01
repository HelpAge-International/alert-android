package org.alertpreparedness.platform.alert.min_preparedness.adapter;

import org.alertpreparedness.platform.alert.min_preparedness.model.Action;

/**
 * Created by Tj on 30/01/2018.
 */

public interface PreparednessAdapter {
    public void addItems(String key, Action action);
    public void removeItem(String key);
}

package org.alertpreparedness.platform.v1.min_preparedness.interfaces;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by faizmohideen on 19/01/2018.
 */

public interface OnItemsChangedListener {
    void onItemChanged(DataSnapshot getChild);
}

package org.alertpreparedness.platform.alert.firebase.wrappers;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by Tj on 14/03/2018.
 */

public class AlertResultWrapper {

    private final String parentId;
    private final boolean isNetwork;
    private final String networkLeadId;
    private DataSnapshot alertSnapshot;

    public AlertResultWrapper(String parentId, boolean isNetwork, String networkLeadId, DataSnapshot alertSnapshot) {

        this.parentId = parentId;
        this.isNetwork = isNetwork;
        this.networkLeadId = networkLeadId;
        this.alertSnapshot = alertSnapshot;
    }

    public String getNetworkLeadId() {
        return networkLeadId;
    }

    public boolean isNetwork() {
        return isNetwork;
    }

    public String getParentId() {
        return parentId;
    }

    public DataSnapshot getAlertSnapshot() {
        return alertSnapshot;
    }

    public void setAlertSnapshot(DataSnapshot alertSnapshot) {
        this.alertSnapshot = alertSnapshot;
    }
}

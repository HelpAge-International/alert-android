package org.alertpreparedness.platform.alert.interfaces;

import org.alertpreparedness.platform.alert.dashboard.model.Alert;
import org.alertpreparedness.platform.alert.dashboard.model.Tasks;
import org.alertpreparedness.platform.alert.firebase.AlertModel;

/**
 * Created by faizmohideen on 28/11/2017.
 */

public interface IHomeActivity {
    void updateAlert(String id, AlertModel alert);
    void removeAlert(String id);
    void addTask(Tasks tasks);
    void updateTitle(int stringResource, int backgroundResource);
}

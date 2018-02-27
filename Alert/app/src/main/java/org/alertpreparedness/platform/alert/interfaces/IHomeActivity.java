package org.alertpreparedness.platform.alert.interfaces;

import org.alertpreparedness.platform.alert.dashboard.model.Task;
import org.alertpreparedness.platform.alert.firebase.AlertModel;

/**
 * Created by faizmohideen on 28/11/2017.
 */

public interface IHomeActivity {
    void updateAlert(String id, AlertModel alert);
    void removeAlert(String id);
    void addTask(String key, Task task);
    void updateTitle(int stringResource, int backgroundResource);
}

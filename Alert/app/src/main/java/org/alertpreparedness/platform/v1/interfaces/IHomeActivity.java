package org.alertpreparedness.platform.v1.interfaces;

import org.alertpreparedness.platform.v1.dashboard.model.Task;
import org.alertpreparedness.platform.v1.firebase.AlertModel;

/**
 * Created by faizmohideen on 28/11/2017.
 */

public interface IHomeActivity {
    void updateAlert(String id, AlertModel alert);
    void removeAlert(String id);
    void addTask(String key, Task task);
    void updateTitle(int stringResource, int backgroundResource);
}

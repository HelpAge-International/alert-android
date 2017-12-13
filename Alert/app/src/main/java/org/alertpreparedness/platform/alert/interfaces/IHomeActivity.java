package org.alertpreparedness.platform.alert.interfaces;

import org.alertpreparedness.platform.alert.dashboard.model.Alert;
import org.alertpreparedness.platform.alert.dashboard.model.Tasks;

/**
 * Created by faizmohideen on 28/11/2017.
 */

public interface IHomeActivity {
    void updateAlert(String id, Alert alert);
    void removeAlert(String id);
    void addTask(Tasks tasks);
    void updateTitle(int stringResource, int backgroundResource);
}

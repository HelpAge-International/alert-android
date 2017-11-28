package org.alertpreparedness.platform.alert.interfaces;

import org.alertpreparedness.platform.alert.model.Alert;
import org.alertpreparedness.platform.alert.model.Tasks;

/**
 * Created by faizmohideen on 28/11/2017.
 */

public interface IHomeActivity {
    void addAlert(Alert alert);
    void addTask(Tasks tasks);
    void updateTitle(int stringResource, int backgroundResource);
}

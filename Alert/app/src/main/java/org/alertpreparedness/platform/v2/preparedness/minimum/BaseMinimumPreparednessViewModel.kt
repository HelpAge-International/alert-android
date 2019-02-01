package org.alertpreparedness.platform.v2.preparedness.minimum

import androidx.annotation.CallSuper
import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.User
import org.alertpreparedness.platform.v2.models.enums.ActionLevel.MPA
import org.alertpreparedness.platform.v2.preparedness.BasePreparednessViewModel

abstract class BaseMinimumPreparednessViewModel : BasePreparednessViewModel() {
    @CallSuper
    override fun filterAction(action: Action, user: User): Boolean {
        return action.actionLevel == MPA
    }
}
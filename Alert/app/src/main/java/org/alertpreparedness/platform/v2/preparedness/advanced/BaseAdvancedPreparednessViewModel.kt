package org.alertpreparedness.platform.v2.preparedness.advanced

import androidx.annotation.CallSuper
import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.User
import org.alertpreparedness.platform.v2.models.enums.ActionLevel.APA
import org.alertpreparedness.platform.v2.preparedness.BasePreparednessViewModel

open class BaseAdvancedPreparednessViewModel : BasePreparednessViewModel() {
    @CallSuper
    override fun filterAction(action: Action, user: User): Boolean {
        return action.actionLevel == APA
    }
}
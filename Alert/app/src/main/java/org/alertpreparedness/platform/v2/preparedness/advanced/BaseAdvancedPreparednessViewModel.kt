package org.alertpreparedness.platform.v2.preparedness.advanced

import androidx.annotation.CallSuper
import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.User
import org.alertpreparedness.platform.v2.models.enums.ActionLevel.APA
import org.alertpreparedness.platform.v2.models.enums.HazardScenario
import org.alertpreparedness.platform.v2.preparedness.BasePreparednessViewModel

abstract class BaseAdvancedPreparednessViewModel : BasePreparednessViewModel() {
    @CallSuper
    override fun filterAction(action: Action,
            user: User,
            hazards: List<HazardScenario>): Boolean {
        return action.actionLevel == APA
    }

    fun isActive(action: Action, hazards: List<HazardScenario>): Boolean {
        return (action.assignedHazards ?: listOf())
                .intersect(hazards)
                .isNotEmpty()
                || (action.assignedHazards == null && hazards.isNotEmpty())
    }
}
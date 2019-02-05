package org.alertpreparedness.platform.v2.preparedness.minimum

import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.User
import org.alertpreparedness.platform.v2.models.enums.HazardScenario

class UnassignedMinimumPreparednessViewModel : BaseMinimumPreparednessViewModel() {
    override fun filterAction(action: Action,
            user: User,
            hazards: List<HazardScenario>): Boolean {
        return super.filterAction(action, user, hazards) &&
                action.assignee == null &&
                !action.isArchived
    }
}
package org.alertpreparedness.platform.v2.preparedness.advanced

import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.User
import org.alertpreparedness.platform.v2.models.enums.HazardScenario

class ExpiredAdvancedPreparednessViewModel : BaseAdvancedPreparednessViewModel() {
    override fun filterAction(action: Action,
            user: User,
            hazards: List<HazardScenario>): Boolean {
        return super.filterAction(action, user, hazards) &&
                action.assignee == user.id &&
                action.getExpirationTime().isBeforeNow &&
                !action.isArchived &&
                isActive(action, hazards)
    }
}
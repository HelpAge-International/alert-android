package org.alertpreparedness.platform.v2.preparedness.advanced

import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.User

class InProgressAdvancedPreparednessViewModel : BaseAdvancedPreparednessViewModel() {
    override fun filterAction(action: Action, user: User): Boolean {
        return super.filterAction(action, user) &&
                !action.isComplete &&
                action.assignee == user.id &&
                action.getExpirationTime().isAfterNow &&
                !action.isArchived
    }
}
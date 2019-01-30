package org.alertpreparedness.platform.v2.preparedness.minimum

import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.User

class CompleteMinimumPreparednessViewModel : BaseMinimumPreparednessViewModel() {
    override fun filterAction(action: Action, user: User): Boolean {
        return super.filterAction(action, user) && action.isComplete && action.assignee == user.id
    }
}
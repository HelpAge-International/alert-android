package org.alertpreparedness.platform.v2.preparedness.advanced

import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.User

class ArchivedAdvancedPreparednessViewModel : BaseAdvancedPreparednessViewModel() {
    override fun filterAction(action: Action, user: User): Boolean {
        //TODO: What is archived?
        return super.filterAction(action, user) && action.isComplete && action.assignee == user.id
    }
}
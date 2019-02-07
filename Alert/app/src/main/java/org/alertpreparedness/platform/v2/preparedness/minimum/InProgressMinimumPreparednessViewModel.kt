package org.alertpreparedness.platform.v2.preparedness.minimum

import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.User
import org.alertpreparedness.platform.v2.models.enums.HazardScenario
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.ATTACHMENTS
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.COMPLETE
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.NOTES
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.REASSIGN

class InProgressMinimumPreparednessViewModel : BaseMinimumPreparednessViewModel() {

    override fun getSelectOptions(): List<PreparednessBottomSheetOption> {
        return listOf(
                COMPLETE,
                REASSIGN,
                NOTES,
                ATTACHMENTS
        )
    }

    override fun filterAction(action: Action,
            user: User,
            hazards: List<HazardScenario>): Boolean {
        return super.filterAction(action, user, hazards) &&
                !action.isComplete &&
                action.assignee == user.id &&
                action.getExpirationTime().isAfterNow &&
                !action.isArchived
    }
}
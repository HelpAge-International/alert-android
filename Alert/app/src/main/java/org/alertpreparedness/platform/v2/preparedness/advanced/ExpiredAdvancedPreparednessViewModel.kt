package org.alertpreparedness.platform.v2.preparedness.advanced

import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.User
import org.alertpreparedness.platform.v2.models.enums.HazardScenario
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.ATTACHMENTS
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.NOTES
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.REASSIGN
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.UPDATE_DUE_DATE

class ExpiredAdvancedPreparednessViewModel : BaseAdvancedPreparednessViewModel() {

    override fun getSelectOptions(): List<PreparednessBottomSheetOption> {
        return listOf(
                UPDATE_DUE_DATE,
                REASSIGN,
                NOTES,
                ATTACHMENTS
        )
    }

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
package org.alertpreparedness.platform.v2.preparedness.advanced
import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.User
import org.alertpreparedness.platform.v2.models.enums.HazardScenario
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.ATTACHMENTS
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.NOTES
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.REASSIGN
import org.alertpreparedness.platform.v2.utils.extensions.isActive

class CompleteAdvancedPreparednessViewModel : BaseAdvancedPreparednessViewModel() {

    override fun getSelectOptions(): List<PreparednessBottomSheetOption> {
        return listOf(
                REASSIGN,
                NOTES,
                ATTACHMENTS
        )
    }


    override fun filterAction(action: Action,
            user: User,
            hazards: List<HazardScenario>): Boolean {
        return super.filterAction(action, user, hazards) &&
                action.isComplete &&
                action.assignee == user.id &&
                action.getExpirationTime().isAfterNow &&
                action.isActive(hazards) &&
                !action.isArchived
    }
}
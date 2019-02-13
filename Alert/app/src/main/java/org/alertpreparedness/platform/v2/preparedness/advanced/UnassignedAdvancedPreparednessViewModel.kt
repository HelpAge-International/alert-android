package org.alertpreparedness.platform.v2.preparedness.advanced
import org.alertpreparedness.platform.v2.models.Action
import org.alertpreparedness.platform.v2.models.User
import org.alertpreparedness.platform.v2.models.enums.HazardScenario
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.ASSIGN
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.ATTACHMENTS
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.NOTES
import org.alertpreparedness.platform.v2.preparedness.advanced.PreparednessBottomSheetOption.UPDATE_DUE_DATE
import org.alertpreparedness.platform.v2.utils.extensions.isActive

class UnassignedAdvancedPreparednessViewModel : BaseAdvancedPreparednessViewModel() {

    override fun getSelectOptions(): List<PreparednessBottomSheetOption> {
        return listOf(
                UPDATE_DUE_DATE,
                ASSIGN,
                NOTES,
                ATTACHMENTS
        )
    }

    override fun filterAction(action: Action,
            user: User,
            hazards: List<HazardScenario>): Boolean {
        return super.filterAction(action, user, hazards) &&
                action.assignee == null &&
                !action.isArchived &&
                action.isActive(hazards)
    }
}
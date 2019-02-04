package org.alertpreparedness.platform.v2.preparedness.advanced

import org.alertpreparedness.platform.v1.R

class UnassignedAdvancedPreparednessFragment :
        BaseAdvancedPreparednessFragment<UnassignedAdvancedPreparednessViewModel>() {

    override fun statusColor(): Int {
        return R.color.alertRed
    }

    override fun statusText(): Int {
        return R.string.unassigned_title
    }

    override fun statusIcon(): Int {
        return R.drawable.preparedness_red
    }

    override fun viewModelClass(): Class<UnassignedAdvancedPreparednessViewModel> {
        return UnassignedAdvancedPreparednessViewModel::class.java
    }
}
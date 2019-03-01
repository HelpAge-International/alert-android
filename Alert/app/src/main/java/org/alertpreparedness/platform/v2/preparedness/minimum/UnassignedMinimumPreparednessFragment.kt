package org.alertpreparedness.platform.v2.preparedness.minimum

import org.alertpreparedness.platform.R

class UnassignedMinimumPreparednessFragment :
        BaseMinimumPreparednessFragment<UnassignedMinimumPreparednessViewModel>() {

    override fun statusColor(): Int {
        return R.color.alertRed
    }

    override fun statusText(): Int {
        return R.string.unassigned_title
    }

    override fun statusIcon(): Int {
        return R.drawable.preparedness_red
    }

    override fun viewModelClass(): Class<UnassignedMinimumPreparednessViewModel> {
        return UnassignedMinimumPreparednessViewModel::class.java
    }
}
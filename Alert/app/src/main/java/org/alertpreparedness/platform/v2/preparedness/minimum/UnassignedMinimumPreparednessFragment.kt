package org.alertpreparedness.platform.v2.preparedness.minimum

import org.alertpreparedness.platform.v1.R

class UnassignedMinimumPreparednessFragment :
        BaseMinimumPreparednessFragment<UnassignedMinimumPreparednessViewModel>() {

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
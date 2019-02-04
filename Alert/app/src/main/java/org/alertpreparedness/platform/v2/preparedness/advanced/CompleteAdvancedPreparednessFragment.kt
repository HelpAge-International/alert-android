package org.alertpreparedness.platform.v2.preparedness.advanced

import org.alertpreparedness.platform.v1.R

class CompleteAdvancedPreparednessFragment :
        BaseAdvancedPreparednessFragment<CompleteAdvancedPreparednessViewModel>() {

    override fun statusColor(): Int {
        return R.color.alertGreen
    }

    override fun statusText(): Int {
        return R.string.completed_title
    }

    override fun statusIcon(): Int {
        return R.drawable.preparedness_green
    }

    override fun viewModelClass(): Class<CompleteAdvancedPreparednessViewModel> {
        return CompleteAdvancedPreparednessViewModel::class.java
    }
}
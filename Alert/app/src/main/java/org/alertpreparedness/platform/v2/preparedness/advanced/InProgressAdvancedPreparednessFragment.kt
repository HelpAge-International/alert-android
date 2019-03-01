package org.alertpreparedness.platform.v2.preparedness.advanced

import org.alertpreparedness.platform.R

class InProgressAdvancedPreparednessFragment :
        BaseAdvancedPreparednessFragment<InProgressAdvancedPreparednessViewModel>() {

    override fun statusColor(): Int {
        return R.color.alertAmber
    }

    override fun statusText(): Int {
        return R.string.in_progress_title
    }

    override fun statusIcon(): Int {
        return R.drawable.preparedness_amber
    }

    override fun viewModelClass(): Class<InProgressAdvancedPreparednessViewModel> {
        return InProgressAdvancedPreparednessViewModel::class.java
    }
}
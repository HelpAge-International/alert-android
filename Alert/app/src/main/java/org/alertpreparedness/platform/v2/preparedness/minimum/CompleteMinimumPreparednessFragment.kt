package org.alertpreparedness.platform.v2.preparedness.minimum

import org.alertpreparedness.platform.R

class CompleteMinimumPreparednessFragment : BaseMinimumPreparednessFragment<CompleteMinimumPreparednessViewModel>() {

    override fun statusColor(): Int {
        return R.color.alertGreen
    }

    override fun statusText(): Int {
        return R.string.completed_title
    }

    override fun statusIcon(): Int {
        return R.drawable.preparedness_green
    }

    override fun viewModelClass(): Class<CompleteMinimumPreparednessViewModel> {
        return CompleteMinimumPreparednessViewModel::class.java
    }
}
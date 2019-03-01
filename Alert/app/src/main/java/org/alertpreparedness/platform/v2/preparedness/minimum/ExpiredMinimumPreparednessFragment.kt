package org.alertpreparedness.platform.v2.preparedness.minimum

import org.alertpreparedness.platform.R

class ExpiredMinimumPreparednessFragment : BaseMinimumPreparednessFragment<ExpiredMinimumPreparednessViewModel>() {

    override fun statusColor(): Int {
        return R.color.alertRed
    }

    override fun statusText(): Int {
        return R.string.expired_title
    }

    override fun statusIcon(): Int {
        return R.drawable.preparedness_red
    }

    override fun viewModelClass(): Class<ExpiredMinimumPreparednessViewModel> {
        return ExpiredMinimumPreparednessViewModel::class.java
    }
}
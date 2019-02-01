package org.alertpreparedness.platform.v2.preparedness.advanced

import org.alertpreparedness.platform.v1.R

class ExpiredAdvancedPreparednessFragment : BaseAdvancedPreparednessFragment<ExpiredAdvancedPreparednessViewModel>() {
    override fun statusText(): Int {
        return R.string.expired_title
    }

    override fun statusIcon(): Int {
        return R.drawable.preparedness_red
    }

    override fun viewModelClass(): Class<ExpiredAdvancedPreparednessViewModel> {
        return ExpiredAdvancedPreparednessViewModel::class.java
    }
}
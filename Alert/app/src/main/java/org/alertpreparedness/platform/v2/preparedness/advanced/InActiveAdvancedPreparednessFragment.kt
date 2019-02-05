package org.alertpreparedness.platform.v2.preparedness.advanced

import org.alertpreparedness.platform.v1.R

class InActiveAdvancedPreparednessFragment :
        BaseAdvancedPreparednessFragment<InActiveAdvancedPreparednessViewModel>() {

    override fun statusColor(): Int {
        return R.color.alertGrey
    }

    override fun statusText(): Int {
        return R.string.inactive_title
    }

    override fun statusIcon(): Int {
        return R.drawable.preparedness_grey
    }

    override fun viewModelClass(): Class<InActiveAdvancedPreparednessViewModel> {
        return InActiveAdvancedPreparednessViewModel::class.java
    }
}
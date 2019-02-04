package org.alertpreparedness.platform.v2.preparedness.advanced

import org.alertpreparedness.platform.v1.R

class ArchivedAdvancedPreparednessFragment :
        BaseAdvancedPreparednessFragment<ArchivedAdvancedPreparednessViewModel>() {

    override fun statusColor(): Int {
        return R.color.alertGrey
    }

    override fun statusText(): Int {
        return R.string.archived_title
    }

    override fun statusIcon(): Int {
        return R.drawable.preparedness_grey
    }

    override fun viewModelClass(): Class<ArchivedAdvancedPreparednessViewModel> {
        return ArchivedAdvancedPreparednessViewModel::class.java
    }
}
package org.alertpreparedness.platform.v2.preparedness.minimum

import org.alertpreparedness.platform.R

class ArchivedMinimumPreparednessFragment : BaseMinimumPreparednessFragment<ArchivedMinimumPreparednessViewModel>() {

    override fun statusColor(): Int {
        return R.color.alertGrey
    }

    override fun statusText(): Int {
        return R.string.archived_title
    }

    override fun statusIcon(): Int {
        return R.drawable.preparedness_grey
    }

    override fun viewModelClass(): Class<ArchivedMinimumPreparednessViewModel> {
        return ArchivedMinimumPreparednessViewModel::class.java
    }
}
package org.alertpreparedness.platform.v2.preparedness

import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v2.base.BaseFragment

class MinimumPreparednessFragment : BaseFragment<MinimumPreparednessViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_preparedness
    }

    override fun viewModelClass(): Class<MinimumPreparednessViewModel> {
        return MinimumPreparednessViewModel::class.java
    }

    override fun observeViewModel() {
    }
}
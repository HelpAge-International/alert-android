package org.alertpreparedness.platform.v2.mycountry

import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v2.base.BaseActivity

class ProgramResultsActivity : BaseActivity<ProgramResultsViewModel>() {
    override fun viewModelClass(): Class<ProgramResultsViewModel> {
        return ProgramResultsViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_program_results
    }

    override fun observeViewModel() {
    }
}

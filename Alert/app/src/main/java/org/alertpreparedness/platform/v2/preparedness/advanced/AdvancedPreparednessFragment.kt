package org.alertpreparedness.platform.v2.preparedness.advanced

import kotlinx.android.synthetic.main.fragment_minimum_preparedness.viewPager
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v2.base.BaseFragment

class AdvancedPreparednessFragment : BaseFragment<AdvancedPreparednessViewModel>() {
    private lateinit var adapter: AdvancedPreparednessPagerAdapter

    override fun getLayoutId(): Int {
        return R.layout.fragment_advanced_preparedness
    }

    override fun viewModelClass(): Class<AdvancedPreparednessViewModel> {
        return AdvancedPreparednessViewModel::class.java
    }

    override fun initViews() {
        super.initViews()

        adapter = AdvancedPreparednessPagerAdapter(
                fragmentManager!!)
        viewPager.adapter = adapter
    }

    override fun observeViewModel() {
        disposables += viewModel
                .addAction()
                .subscribe {
                    //TODO
                }
    }
}
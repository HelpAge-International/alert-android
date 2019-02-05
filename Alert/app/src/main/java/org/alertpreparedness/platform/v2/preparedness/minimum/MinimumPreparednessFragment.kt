package org.alertpreparedness.platform.v2.preparedness.minimum

import kotlinx.android.synthetic.main.fragment_minimum_preparedness.viewPager
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v2.base.BaseFragment

class MinimumPreparednessFragment : BaseFragment<MinimumPreparednessViewModel>() {
    private lateinit var adapter: MinimumPreparednessPagerAdapter

    override fun getLayoutId(): Int {
        return R.layout.fragment_minimum_preparedness
    }

    override fun viewModelClass(): Class<MinimumPreparednessViewModel> {
        return MinimumPreparednessViewModel::class.java
    }

    override fun initViews() {
        super.initViews()

        adapter = MinimumPreparednessPagerAdapter(
                fragmentManager!!)
        viewPager.adapter = adapter
    }

    override fun observeViewModel() {
    }
}
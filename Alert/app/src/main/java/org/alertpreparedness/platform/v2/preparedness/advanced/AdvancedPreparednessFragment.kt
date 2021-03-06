package org.alertpreparedness.platform.v2.preparedness.advanced

import android.content.Intent
import kotlinx.android.synthetic.main.fragment_advanced_preparedness.btnAdd
import kotlinx.android.synthetic.main.fragment_minimum_preparedness.viewPager
import org.alertpreparedness.platform.v1.MainDrawer
import org.alertpreparedness.platform.R
import org.alertpreparedness.platform.v1.adv_preparedness.activity.CreateAPAActivity
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

        (activity as MainDrawer).toggleActionBarWithTitle(MainDrawer.ActionBarState.NORMAL,
                R.string.title_adv_preparedness)
        (activity as MainDrawer).showActionbarElevation()

        btnAdd.setOnClickListener {
            viewModel.addButtonClicked()
        }

        adapter = AdvancedPreparednessPagerAdapter(
                fragmentManager!!)
        viewPager.adapter = adapter
    }

    override fun observeViewModel() {
        disposables += viewModel
                .addAction()
                .subscribe {
                    val intent = Intent(activity, CreateAPAActivity::class.java)
                    startActivity(intent)
                }
    }
}
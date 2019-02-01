package org.alertpreparedness.platform.v2.preparedness

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_page_preparedness.ivStatus
import kotlinx.android.synthetic.main.fragment_page_preparedness.rvActions
import kotlinx.android.synthetic.main.fragment_page_preparedness.tvStatus
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v2.base.BaseFragment
import org.jetbrains.anko.imageResource

abstract class BasePreparednessFragment<VM : BasePreparednessViewModel> : BaseFragment<VM>() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_page_preparedness
    }

    @StringRes
    abstract fun statusText(): Int

    @DrawableRes
    abstract fun statusIcon(): Int

    private lateinit var adapter: PreparednessAdapter

    override fun initViews() {
        super.initViews()

        tvStatus.setText(statusText())
        ivStatus.imageResource = statusIcon()

        adapter = PreparednessAdapter(context!!)
        rvActions.adapter = adapter
        rvActions.layoutManager = LinearLayoutManager(context!!)
    }

    override fun observeViewModel() {
        disposables += viewModel.user()
                .subscribe {
                    adapter.updateUser(it)
                }

        disposables += viewModel.actions()
                .subscribe {
                    adapter.updateItems(it)
                }
    }
}
package org.alertpreparedness.platform.v2.preparedness

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_page_preparedness.ivStatus
import kotlinx.android.synthetic.main.fragment_page_preparedness.rvActions
import kotlinx.android.synthetic.main.fragment_page_preparedness.tvNoAction
import kotlinx.android.synthetic.main.fragment_page_preparedness.tvStatus
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v2.base.BaseFragment
import org.alertpreparedness.platform.v2.utils.extensions.show
import org.jetbrains.anko.imageResource

abstract class BasePreparednessFragment<VM : BasePreparednessViewModel> : BaseFragment<VM>() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_page_preparedness
    }

    @StringRes
    abstract fun statusText(): Int

    @DrawableRes
    abstract fun statusIcon(): Int

    @ColorRes
    abstract fun statusColor(): Int

    private lateinit var adapter: PreparednessAdapter

    override fun initViews() {
        super.initViews()

        tvStatus.setText(statusText())
        tvStatus.setTextColor(getColor(context!!, statusColor()))
        ivStatus.imageResource = statusIcon()

        adapter = PreparednessAdapter(context!!)
        rvActions.adapter = adapter
        rvActions.layoutManager = LinearLayoutManager(context!!)
        rvActions.addItemDecoration(DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL))
    }

    override fun observeViewModel() {
        disposables += viewModel.user()
                .subscribe {
                    adapter.updateUser(it)
                }

        disposables += viewModel.actions()
                .subscribe {
                    adapter.updateItems(it)
                    rvActions.show(it.isNotEmpty())
                    tvNoAction.show(it.isEmpty())
                }
    }
}
package org.alertpreparedness.platform.v2.alert

import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_alert_detail.btnApprove
import kotlinx.android.synthetic.main.activity_alert_detail.btnReject
import kotlinx.android.synthetic.main.activity_alert_detail.grpApproveReject
import kotlinx.android.synthetic.main.activity_alert_detail.rvAlertDetails
import kotlinx.android.synthetic.main.activity_alert_detail.tvBanner
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v2.base.BaseActivity
import org.alertpreparedness.platform.v2.utils.extensions.isRedAlertRequested
import org.alertpreparedness.platform.v2.utils.extensions.show

class AlertDetailActivity : BaseActivity<AlertDetailViewModel>() {
    private lateinit var alertId: String
    private lateinit var adapter: AlertDetailsAdapter

    override fun viewModelClass(): Class<AlertDetailViewModel> {
        return AlertDetailViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_alert_detail
    }

    override fun initViews() {
        super.initViews()

        adapter = AlertDetailsAdapter(this)
        rvAlertDetails.adapter = adapter
        rvAlertDetails.layoutManager = LinearLayoutManager(this)

        btnApprove.setOnClickListener {
            viewModel.approveClicked()
        }

        btnReject.setOnClickListener {
            viewModel.rejectClicked()
        }

        viewModel.alertId(alertId)

        setToolbarTitle(R.string.alert)
        showBackButton(true)
    }

    override fun observeViewModel() {
        disposables += viewModel.alert()
                .subscribe { alert ->
                    adapter.update(alert)

                    var toolbarColor = alert.level.color
                    if (alert.isRedAlertRequested()) {
                        toolbarColor = R.color.alertGrey
                    }

                    setToolbarTitle(alert.level.string, ContextCompat.getColor(this, toolbarColor))
                }

        disposables += viewModel.showRequestBanner()
                .subscribe {
                    val requestBanner = it.value

                    tvBanner.show(requestBanner != null)
                    requestBanner?.let {
                        tvBanner.text = getString(
                                R.string.red_alert_requested_banner,
                                requestBanner.requestee.firstName + " " + requestBanner.requestee.lastName,
                                getString(requestBanner.previousLevel.string),
                                getString(requestBanner.requestedLevel.string),
                                requestBanner.dateOfRequest.toDate()
                        )
                    }
                }

        disposables += viewModel.showApproveReject()
                .subscribe {
                    grpApproveReject.show(it)
                }

        disposables += viewModel.editAlert()
                .subscribe {
                    //TODO: Start edit activity
                }
    }

    override fun arguments(bundle: Bundle) {
        alertId = bundle[ALERT_ID_KEY] as String
    }

    companion object {
        const val ALERT_ID_KEY = "ALERT_ID"
    }

    override fun getToolbarMenu(): Int? {
        return R.menu.edit
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.btnEdit) {
            viewModel.editClicked()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

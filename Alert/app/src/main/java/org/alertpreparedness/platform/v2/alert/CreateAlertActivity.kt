package org.alertpreparedness.platform.v2.alert

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_create_alert.etInformation
import kotlinx.android.synthetic.main.activity_create_alert.etPopulation
import kotlinx.android.synthetic.main.activity_create_alert.etRedAlertReason
import kotlinx.android.synthetic.main.activity_create_alert.tvAlertLevel
import kotlinx.android.synthetic.main.activity_create_alert.tvHazard
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v2.base.BaseActivity
import org.alertpreparedness.platform.v2.models.Area
import org.alertpreparedness.platform.v2.utils.extensions.bind
import org.alertpreparedness.platform.v2.utils.extensions.bindStringResource

class CreateAlertActivity : BaseActivity<CreateAlertViewModel>(), AreaListener {
    companion object {

        const val ALERT_ID_KEY = "ALERT_ID_KEY"
    }

    private var areaAdapter: CreateAlertAreaAdapter(this, this)

    private var alertId: String? = null

    override fun viewModelClass(): Class<CreateAlertViewModel> {
        return CreateAlertViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_create_alert
    }

    override fun arguments(bundle: Bundle) {
        super.arguments(bundle)

        alertId = bundle.getString(ALERT_ID_KEY, null)
    }

    override fun initViews() {
        super.initViews()

        viewModel.baseAlert(alertId)
    }

    override fun observeViewModel() {
        disposables += tvHazard.bindStringResource(viewModel.hazardScenario().map { it.text })
        disposables += tvAlertLevel.bindStringResource(viewModel.alertLevel().map { it.string })
        disposables += etRedAlertReason.bind(viewModel.redAlertReason())
        disposables += etPopulation.bind(viewModel.populationAffected().map { it.toString() })
        disposables += etInformation.bind(viewModel.informationSources())

        disposables += viewModel.affectedAreas()
                .subscribe {
                    areaAdapter.updateItems(it)
                }

        disposables += viewModel.addArea()
                .subscribe {

                }
    }

    override fun onAreaRemoved(area: Area) {
        viewModel.onAreaRemoved(area)
    }

    override fun onAddAreaClicked() {
        viewModel.onAddAreaClicked()
    }
}

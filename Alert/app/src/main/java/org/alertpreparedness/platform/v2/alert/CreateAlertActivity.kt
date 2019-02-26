package org.alertpreparedness.platform.v2.alert

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_create_alert.btnAlertLevel
import kotlinx.android.synthetic.main.activity_create_alert.btnHazard
import kotlinx.android.synthetic.main.activity_create_alert.btnSave
import kotlinx.android.synthetic.main.activity_create_alert.clRedReason
import kotlinx.android.synthetic.main.activity_create_alert.dividerRedReason
import kotlinx.android.synthetic.main.activity_create_alert.etInformation
import kotlinx.android.synthetic.main.activity_create_alert.etPopulation
import kotlinx.android.synthetic.main.activity_create_alert.etRedAlertReason
import kotlinx.android.synthetic.main.activity_create_alert.ivAlertLevel
import kotlinx.android.synthetic.main.activity_create_alert.rvAreas
import kotlinx.android.synthetic.main.activity_create_alert.tvAlertLevel
import kotlinx.android.synthetic.main.activity_create_alert.tvHazard
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v1.dashboard.activity.HazardSelectionActivity
import org.alertpreparedness.platform.v1.dashboard.activity.HazardSelectionActivity.HAZARD_TYPE
import org.alertpreparedness.platform.v1.helper.AlertLevelDialog
import org.alertpreparedness.platform.v1.risk_monitoring.model.ModelIndicatorLocation
import org.alertpreparedness.platform.v1.risk_monitoring.view.SelectAreaActivity
import org.alertpreparedness.platform.v2.alert.ButtonState.CONFIRM_LEVEL
import org.alertpreparedness.platform.v2.alert.ButtonState.CONFIRM_RED_LEVEL
import org.alertpreparedness.platform.v2.alert.ButtonState.REQUEST_RED_LEVEL
import org.alertpreparedness.platform.v2.alert.ButtonState.SAVE_CHANGES
import org.alertpreparedness.platform.v2.alert.SaveState.MISSING_AREAS
import org.alertpreparedness.platform.v2.alert.SaveState.MISSING_HAZARD
import org.alertpreparedness.platform.v2.alert.SaveState.MISSING_INFORMATION
import org.alertpreparedness.platform.v2.alert.SaveState.MISSING_LEVEL
import org.alertpreparedness.platform.v2.alert.SaveState.MISSING_POPULATION
import org.alertpreparedness.platform.v2.alert.SaveState.MISSING_RED_ALERT_REASON
import org.alertpreparedness.platform.v2.alert.SaveState.SUCCESS
import org.alertpreparedness.platform.v2.base.BaseActivity
import org.alertpreparedness.platform.v2.models.Area
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.AMBER
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.GREEN
import org.alertpreparedness.platform.v2.models.enums.AlertLevel.RED
import org.alertpreparedness.platform.v2.models.enums.AlertLevelSerializer
import org.alertpreparedness.platform.v2.models.enums.HazardScenarioSerializer
import org.alertpreparedness.platform.v2.utils.extensions.afterTextChanged
import org.alertpreparedness.platform.v2.utils.extensions.bind
import org.alertpreparedness.platform.v2.utils.extensions.bindRes
import org.alertpreparedness.platform.v2.utils.extensions.bindStringResource
import org.alertpreparedness.platform.v2.utils.extensions.isRedAlertRequested
import org.alertpreparedness.platform.v2.utils.extensions.show
import org.alertpreparedness.platform.v2.utils.filterNull

class CreateAlertActivity : BaseActivity<CreateAlertViewModel>(), AreaListener {
    companion object {

        const val ALERT_ID_KEY = "ALERT_ID_KEY"
        const val AREA_REQUEST_CODE = 1000
        const val HAZARD_REQUEST_CODE = 1001
    }

    private lateinit var alertLevelDialog: AlertLevelDialog

    private var areaAdapter = CreateAlertAreaAdapter(this, this)

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

        alertLevelDialog = AlertLevelDialog()
        alertLevelDialog.setListener {
            val alertLevel = AlertLevelSerializer.deserialize(it)!!
            viewModel.onAlertLevelUpdate(alertLevel)
        }

        btnAlertLevel.setOnClickListener {
            viewModel.onAlertLevelClicked()
        }

        btnHazard.setOnClickListener {
            viewModel.onHazardScenarioClicked()
        }

        btnSave.setOnClickListener {
            viewModel.onSaveClicked()
        }

        etPopulation.afterTextChanged {
            viewModel.onPopulationAffectedUpdate(it.toLong())
        }

        etRedAlertReason.afterTextChanged {
            viewModel.onRedAlertReasonUpdate(it)
        }

        etInformation.afterTextChanged {
            viewModel.onInformationSourcesUpdate(it)
        }

        rvAreas.adapter = areaAdapter
        rvAreas.layoutManager = LinearLayoutManager(this)

        setToolbarTitle(
                if (alertId == null) R.string.title_activity_create_alert else R.string.title_activity_edit_alert)
        showBackButton(true, R.drawable.ic_close_white_24dp)

        viewModel.baseAlert(alertId)
    }

    override fun observeViewModel() {
        disposables += tvHazard.bindStringResource(viewModel.hazardScenario().map { it.text })
        disposables += tvAlertLevel.bindStringResource(viewModel.alertLevel().map { it.string })
        disposables += ivAlertLevel.bindRes(viewModel.alertLevel().map {
            when (it) {
                RED -> R.drawable.alert_red_icon
                AMBER -> R.drawable.alert_amber_icon
                GREEN -> R.drawable.alert_green_icon
            }
        })
        disposables += etRedAlertReason.bind(viewModel.redAlertReason())
        disposables += etPopulation.bind(viewModel.populationAffected().map { it.toString() })
        disposables += etInformation.bind(viewModel.informationSources())

        disposables += viewModel.baseAlert()
                .filterNull()
                .subscribe { alert ->

                    var toolbarColor = alert.level.color
                    if (alert.isRedAlertRequested()) {
                        toolbarColor = R.color.alertGrey
                    }

                    setToolbarColor(ContextCompat.getColor(this, toolbarColor))
                }


        disposables += viewModel.affectedAreas()
                .subscribe {
                    areaAdapter.updateItems(it)
                }

        disposables += viewModel.addArea()
                .subscribe {
                    startActivityForResult(Intent(this, SelectAreaActivity::class.java), AREA_REQUEST_CODE)
                }

        disposables += viewModel.selectAlertLevel()
                .subscribe {
                    alertLevelDialog.show(supportFragmentManager, "alertLevelDialog")
                }

        disposables += viewModel.selectHazardScenario()
                .subscribe {
                    startActivityForResult(Intent(this, HazardSelectionActivity::class.java), HAZARD_REQUEST_CODE)
                }

        disposables += viewModel.showRedAlertReason()
                .subscribe { showRedAlertReason ->
                    dividerRedReason.show(showRedAlertReason)
                    clRedReason.show(showRedAlertReason)
                }

        disposables += viewModel.buttonState()
                .subscribe {
                    when (it) {
                        CONFIRM_LEVEL -> btnSave.setText(R.string.confirm_alert_level)
                        CONFIRM_RED_LEVEL -> btnSave.setText(R.string.confirm_red_alert_level)
                        SAVE_CHANGES -> btnSave.setText(R.string.save_changes)
                        REQUEST_RED_LEVEL -> btnSave.setText(R.string.request_red_alert)
                    }
                }

        disposables += viewModel.saveState()
                .subscribe {
                    when (it) {
                        SUCCESS -> finish()
                        MISSING_HAZARD -> Toasty.error(this, getString(R.string.error_missing_hazard)).show()
                        MISSING_LEVEL -> Toasty.error(this, getString(R.string.error_missing_alert_level)).show()
                        MISSING_RED_ALERT_REASON -> Toasty.error(this,
                                getString(R.string.error_missing_red_alert_reason)).show()
                        MISSING_POPULATION -> Toasty.error(this, getString(R.string.error_missing_population)).show()
                        MISSING_AREAS -> Toasty.error(this, getString(R.string.error_missing_affected_areas)).show()
                        MISSING_INFORMATION -> Toasty.error(this,
                                getString(R.string.error_missing_information)).show()
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                AREA_REQUEST_CODE -> {
                    val area = data?.getParcelableExtra<ModelIndicatorLocation>(
                            SelectAreaActivity.SELECTED_AREA)?.toV2()
                    area?.let { viewModel.onAreaAdded(area) }
                }
                HAZARD_REQUEST_CODE -> {
                    val hazardType = HazardScenarioSerializer.deserialize(data?.getIntExtra(HAZARD_TYPE, 0))!!
                    viewModel.onHazardScenarioUpdate(hazardType)
                }
            }
        }
    }

    override fun onAreaRemoved(area: Area) {
        viewModel.onAreaRemoved(area)
    }

    override fun onAddAreaClicked() {
        viewModel.onAddAreaClicked()
    }
}

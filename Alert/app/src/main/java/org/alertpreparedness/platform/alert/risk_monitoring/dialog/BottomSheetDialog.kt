package org.alertpreparedness.platform.alert.risk_monitoring.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import es.dmoral.toasty.Toasty
import io.reactivex.Observable
import kotlinx.android.synthetic.main.bottom_sheet_indicator.view.*
import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.R
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelIndicator
import org.alertpreparedness.platform.alert.risk_monitoring.view.ActiveRiskFragment
import org.alertpreparedness.platform.alert.risk_monitoring.view.AddIndicatorActivity
import org.alertpreparedness.platform.alert.risk_monitoring.view.IndicatorLogActivity
import org.alertpreparedness.platform.alert.risk_monitoring.view.UpdateIndicatorActivity
import org.alertpreparedness.platform.alert.risk_monitoring.view_model.ActiveRiskViewModel
import org.alertpreparedness.platform.alert.utils.Constants
import org.alertpreparedness.platform.alert.utils.PermissionsHelper
import org.alertpreparedness.platform.alert.utils.PreferHelper
import org.jetbrains.anko.find
import q.rorbin.badgeview.QBadgeView
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by fei on 22/11/2017.
 */
class BottomSheetDialog : BottomSheetDialogFragment() {

    private lateinit var ivIndicatorLog: ImageView
    private lateinit var ivIndicatorAttach: ImageView
    private lateinit var flIndicatorLog: FrameLayout
    private lateinit var flIndicatorAttach: FrameLayout
    private lateinit var llInformationSource: LinearLayout
    private lateinit var mViewModel: ActiveRiskViewModel
    private lateinit var mIndicatorModel: ModelIndicator
    private lateinit var  mCountryId : String
    private var mHazardId = ""
    private var mIndicatorId = ""
    private var mNetworkId:String? = null
    private var mNetworkCountryId:String? = null

    @Inject
    lateinit var permissions : PermissionsHelper

    companion object {
        val INDICATOR_MODEL = "indicator_model"
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val view = View.inflate(activity, R.layout.bottom_sheet_indicator, null)
        initData()
        initView(view)
        initListeners(view)
        dialog.setContentView(view)
    }

    private fun initData() {
        mCountryId = PreferHelper.getString(context, Constants.COUNTRY_ID)
        Timber.d("country id: %s", mCountryId)
        if (arguments.containsKey(ActiveRiskFragment.HAZARD_ID)) {
            mHazardId = arguments.get(ActiveRiskFragment.HAZARD_ID) as String
        }
        if (arguments.containsKey(ActiveRiskFragment.INDICATOR_ID)) {
            mIndicatorId = arguments.get(ActiveRiskFragment.INDICATOR_ID) as String
        }
        if (arguments.containsKey(ActiveRiskFragment.NETWORK_ID)) {
            mNetworkId = arguments.get(ActiveRiskFragment.NETWORK_ID) as String
        }
        if (arguments.containsKey(ActiveRiskFragment.NETWORK_COUNTRY_ID)) {
            mNetworkCountryId = arguments.get(ActiveRiskFragment.NETWORK_COUNTRY_ID) as String
        }
        Timber.d("hazardId: %s, indicatorId: %s", mHazardId, mIndicatorId)
        mViewModel = ViewModelProviders.of(this).get(ActiveRiskViewModel::class.java)
        mHazardId = if (mHazardId == "countryContext") mCountryId else mHazardId
        mViewModel.getLiveIndicatorModel(mHazardId, mIndicatorId).observe(this, Observer { indicator ->
            indicator?.let { mIndicatorModel = indicator }
        })
    }

    private fun initView(view: View) {
        ivIndicatorLog = view.find(R.id.ivIndicatorLog)
//        ivIndicatorAttach = view.find(R.id.ivIndicatorAttach)
        flIndicatorLog = view.find(R.id.flIndicatorLog)
//        flIndicatorAttach = view.find(R.id.flIndicatorAttach)
        llInformationSource = view.find(R.id.llInformationSource)

        val logBadge = QBadgeView(activity)
        logBadge.bindTarget(flIndicatorLog).badgeGravity = Gravity.END or Gravity.TOP
        logBadge.setBadgeTextSize(7.toFloat(), true)

        mViewModel.getLiveIndicatorLogs(mIndicatorId).observe(this, Observer { logs ->
            logBadge.badgeNumber = logs?.size ?: 0
        })

    }

    private fun initListeners(view: View) {
        llInformationSource.setOnClickListener {
            if (mHazardId.isNotEmpty() && mIndicatorId.isNotEmpty()) {
                val showInfoDialog = ShowInformationSourceDialog()
                val bundle = Bundle()
                bundle.putParcelable(INDICATOR_MODEL, mIndicatorModel)
                showInfoDialog.arguments = bundle
                showInfoDialog.show(fragmentManager, "show_info_source_dialog")
                dismiss()
            } else {
                Toasty.error(activity, "No hazard id or indicator id!").show()
            }
        }

        view.llUpdateIndicator.setOnClickListener {
            Timber.d("update clicked")
            dismiss()
            UpdateIndicatorActivity.startActivity(activity, mHazardId, mIndicatorId)
        }

        view.llIndicatorLog.setOnClickListener {
            Timber.d("start log activity with id: %s", mIndicatorId)
            dismiss()
            if(permissions.checkEditIndicator(activity)) {
                IndicatorLogActivity.startActivity(activity, mIndicatorId, mIndicatorModel.triggerSelected)
            }
        }

        view.llEditIndicator.setOnClickListener {
            dismiss()
            AddIndicatorActivity.startActivityWithValues(activity, mHazardId, mIndicatorId, mNetworkId, mNetworkCountryId)
        }
    }

}
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
import org.alertpreparedness.platform.alert.R
import org.alertpreparedness.platform.alert.helper.UserInfo
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelIndicator
import org.alertpreparedness.platform.alert.risk_monitoring.view.ActiveRiskFragment
import org.alertpreparedness.platform.alert.risk_monitoring.view_model.ActiveRiskViewModel
import org.jetbrains.anko.find
import q.rorbin.badgeview.QBadgeView

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
    private var mHazardId = ""
    private var mIndicatorId = ""

    companion object {
        val INDICATOR_MODEL = "indicator_model"
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mHazardId = arguments[ActiveRiskFragment.HAZARD_ID] as String
        mIndicatorId = arguments[ActiveRiskFragment.INDICATOR_ID] as String
        mViewModel = ViewModelProviders.of(this).get(ActiveRiskViewModel::class.java)
        mHazardId = if (mHazardId == "Country Context") UserInfo.getUser(activity).countryID else mHazardId
        mViewModel.getLiveIndicatorModel(mHazardId, mIndicatorId).observe(this, Observer { indicator ->
            indicator?.let { mIndicatorModel = indicator }
        })
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val view = View.inflate(activity, R.layout.bottom_sheet_indicator, null)
        initView(view)
        initListeners()
        dialog.setContentView(view)
    }

    private fun initView(view: View) {
        ivIndicatorLog = view.find(R.id.ivIndicatorLog)
        ivIndicatorAttach = view.find(R.id.ivIndicatorAttach)
        flIndicatorLog = view.find(R.id.flIndicatorLog)
        flIndicatorAttach = view.find(R.id.flIndicatorAttach)
        llInformationSource = view.find(R.id.llInformationSource)

        //TODO IMPLEMENT THIS WITH DATA
        val logBadge = QBadgeView(activity)
        logBadge.bindTarget(flIndicatorLog).badgeGravity = Gravity.END or Gravity.TOP
        logBadge.badgeNumber = 3
        logBadge.setBadgeTextSize(7.toFloat(), true)

        val attBadge = QBadgeView(activity)
        attBadge.bindTarget(flIndicatorAttach).badgeGravity = Gravity.END or Gravity.TOP
        attBadge.badgeNumber = 1
        attBadge.setBadgeTextSize(7.toFloat(), true)

    }

    private fun initListeners() {
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
    }

}
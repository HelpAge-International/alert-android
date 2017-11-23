package org.alertpreparedness.platform.alert.risk_monitoring.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import io.reactivex.Observable
import org.alertpreparedness.platform.alert.R
import org.alertpreparedness.platform.alert.risk_monitoring.view.ActiveRiskFragment
import org.alertpreparedness.platform.alert.utils.Constants
import org.jetbrains.anko.find
import q.rorbin.badgeview.QBadgeView
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Created by fei on 22/11/2017.
 */
class BottomSheetDialog:BottomSheetDialogFragment() {

    private lateinit var ivIndicatorLog:ImageView
    private lateinit var ivIndicatorAttach:ImageView
    private lateinit var flIndicatorLog:FrameLayout
    private lateinit var flIndicatorAttach:FrameLayout
    private lateinit var llInformationSource:LinearLayout
    private var mHazardId = arguments[ActiveRiskFragment.HAZARD_ID]?:""
    private var mIndicatorId = arguments[ActiveRiskFragment.INDICATOR_ID]?:""

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
        ivIndicatorAttach= view.find(R.id.ivIndicatorAttach)
        flIndicatorLog = view.find(R.id.flIndicatorLog)
        flIndicatorAttach = view.find(R.id.flIndicatorAttach)
        llInformationSource = view.find(R.id.llInformationSource)

        //TODO IMPLEMENT THIS WITH DATA
        val logBadge = QBadgeView(activity)
        logBadge.bindTarget(flIndicatorLog).badgeGravity = Gravity.END or Gravity.TOP
        logBadge.badgeNumber = 3
        logBadge.setBadgeTextSize(7.toFloat(),true)

        val attBadge = QBadgeView(activity)
        attBadge.bindTarget(flIndicatorAttach).badgeGravity = Gravity.END or Gravity.TOP
        attBadge.badgeNumber = 1
        attBadge.setBadgeTextSize(7.toFloat(),true)

    }

    private fun initListeners() {
        llInformationSource.setOnClickListener {
            dismiss()
            Observable.timer(Constants.MENU_CLOSING_DURATION, TimeUnit.MILLISECONDS).subscribe({
                Timber.d("start activity!!!")
            })
        }
    }

}
package org.alertpreparedness.platform.alert.risk_monitoring.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import org.alertpreparedness.platform.alert.R
import org.jetbrains.anko.find
import q.rorbin.badgeview.QBadgeView

/**
 * Created by fei on 22/11/2017.
 */
class BottomSheetDialog:BottomSheetDialogFragment() {

    private lateinit var ivIndicatorLog:ImageView
    private lateinit var ivIndicatorAttach:ImageView
    private lateinit var flIndicatorLog:FrameLayout
    private lateinit var flIndicatorAttach:FrameLayout

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val view = View.inflate(activity, R.layout.bottom_sheet_indicator, null)
        initView(view)
        dialog.setContentView(view)
    }

    private fun initView(view: View) {
        ivIndicatorLog = view.find(R.id.ivIndicatorLog)
        ivIndicatorAttach= view.find(R.id.ivIndicatorAttach)
        flIndicatorLog = view.find(R.id.flIndicatorLog)
        flIndicatorAttach = view.find(R.id.flIndicatorAttach)
        val logBadge = QBadgeView(activity)
        logBadge.bindTarget(flIndicatorLog).badgeGravity = Gravity.END or Gravity.TOP
        logBadge.badgeNumber = 3
        logBadge.setBadgeTextSize(7.toFloat(),true)

        val attBadge = QBadgeView(activity)
        attBadge.bindTarget(flIndicatorAttach).badgeGravity = Gravity.END or Gravity.TOP
        attBadge.badgeNumber = 1
        attBadge.setBadgeTextSize(7.toFloat(),true)

    }

}
package org.alertpreparedness.platform.alert.risk_monitoring.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.View
import org.alertpreparedness.platform.alert.R

/**
 * Created by fei on 22/11/2017.
 */
class BottomSheetDialog:BottomSheetDialogFragment() {

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val view = View.inflate(activity, R.layout.bottom_sheet_indicator, null)
        dialog.setContentView(view)
    }

}
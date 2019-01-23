package org.alertpreparedness.platform.v1.risk_monitoring.dialog

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import timber.log.Timber

/**
 * Created by fei on 08/11/2017.
 */
class HazardSelectionDialog() : DialogFragment() {

    private val selection:Array<String> = arrayOf("test1","test2","test3")
    private var listener: HazardSelectionListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        return AlertDialog.Builder(activity)
                .setItems(selection){dialog,position ->
                    run {
                        Timber.d("dialog clicked: %s", selection[position])
                        listener?.selectedHazardId(selection[position])
                    }
                }
                .create()
    }

    fun setOnSelectionListener(passedListener: HazardSelectionListener) {
        listener = passedListener
    }
}

interface HazardSelectionListener {
    fun selectedHazardId(hazardId:String)
}
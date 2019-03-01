package org.alertpreparedness.platform.v1.risk_monitoring.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import org.alertpreparedness.platform.R
import org.alertpreparedness.platform.v1.risk_monitoring.view.AddIndicatorActivity
import timber.log.Timber

/**
 * Created by fei on 08/11/2017.
 */

class LocationSelectionDialogFragment : DialogFragment() {

    private var mListener: OnLocationSelected? = null

    private var mPosition: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        if (arguments != null) {
            mPosition = arguments!!.getInt(AddIndicatorActivity.SELECTED_LOCATION)
        }

        return AlertDialog.Builder(activity)
                .setTitle("Location selection")
                .setSingleChoiceItems(arrayOf("National", "Subnational", "Use my location"), mPosition) { _, position ->
                    Timber.d("position: %s", position)
                    mPosition = position
                }
                .setPositiveButton(getString(R.string.save), { _, _ ->
                    mListener?.locationSelected(mPosition)
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
    }

    fun setOnLocationSelectedListener(listener: OnLocationSelected?) {
        mListener = listener
    }

}

interface OnLocationSelected {
    fun locationSelected(location: Int)
}
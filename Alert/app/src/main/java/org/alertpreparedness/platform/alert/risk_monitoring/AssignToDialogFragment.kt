package org.alertpreparedness.platform.alert.risk_monitoring


import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import org.alertpreparedness.platform.alert.R
import timber.log.Timber

/**
 * Created by fei on 08/11/2017.
 */
class AssignToDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        return AlertDialog.Builder(activity)
                .setTitle(getString(R.string.assign_to))
                .setSingleChoiceItems(arrayOf<String>("id1", "id2","d3","d4","d5","d6","d7","d8","d9"),0) { p0, p1 -> Timber.d("selected person: %s", p1) }
                .setPositiveButton(getString(R.string.save), null)
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
    }
}
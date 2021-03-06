package org.alertpreparedness.platform.v1.risk_monitoring.dialog


import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.widget.ArrayAdapter
import org.alertpreparedness.platform.R
import org.alertpreparedness.platform.v1.risk_monitoring.model.ModelUserPublic
import org.alertpreparedness.platform.v1.risk_monitoring.view.AddIndicatorActivity
import timber.log.Timber
import java.util.*

/**
 * Created by fei on 08/11/2017.
 */
class AssignToDialogFragment : DialogFragment() {

    private var mListener: AssignToListener? = null
//    private val data = arrayOf<String>("id1", "id2", "d3", "d4", "d5", "d6", "d7", "d8", "d9")
    private var mPosition = 0
    private var defaultPosition = 0
    private var mStaff: ArrayList<ModelUserPublic>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        if (arguments != null) {
            defaultPosition = arguments!!.getInt(AddIndicatorActivity.ASSIGN_POSITION)
            if (arguments!!.getSerializable(AddIndicatorActivity.STAFF_SELECTION) != null) {
                mStaff = arguments!!.getSerializable(AddIndicatorActivity.STAFF_SELECTION) as ArrayList<ModelUserPublic>
                if (mStaff?.first()?.firstName != "Unassigned") {
                    mStaff!!.add(0, ModelUserPublic(firstName = "Unassigned"))
                }
            }
        }
        val adapter = if (mStaff != null) ArrayAdapter<String>(activity, android.R.layout.simple_list_item_single_choice, mStaff?.map { it.firstName + " " + it.lastName }) else ArrayAdapter<ModelUserPublic>(activity, android.R.layout.simple_list_item_single_choice, emptyArray())
        return AlertDialog.Builder(activity)
                .setTitle(getString(R.string.assign_to))
                .setSingleChoiceItems(adapter, defaultPosition) { _, p1 ->
                    Timber.d("selected person: %s", p1)
                    mPosition = p1
                }
                .setPositiveButton(getString(R.string.save)) { _, _ ->
                    Timber.d("save button clicked: %s", mPosition)
                    mListener?.userAssignedTo(mStaff?.get(mPosition), mPosition)
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
    }

    fun setOnAssignToListener(listener: AssignToListener) {
        mListener = listener
    }
}

interface AssignToListener {
    fun userAssignedTo(user: ModelUserPublic?, position: Int)
}
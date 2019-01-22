package org.alertpreparedness.platform.alert.risk_monitoring.dialog

import android.R
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.ArrayAdapter
import org.alertpreparedness.platform.alert.risk_monitoring.model.CountryJsonData
import org.alertpreparedness.platform.alert.risk_monitoring.model.LevelOneValuesItem
import org.alertpreparedness.platform.alert.risk_monitoring.view.SelectAreaActivity

/**
 * Created by Fei on 11/11/2017.
 */
class SelectLevel1Dialog: DialogFragment() {

    private var mListener: OnLevel1SelectedListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        @Suppress("UNCHECKED_CAST")
        val countryDataList = arguments!!.getSerializable(SelectAreaActivity.SELECT_DIALOG_ARGS) as ArrayList<CountryJsonData>
        val countryId = arguments!!.getInt(SelectAreaActivity.SELECT_LEVEL1_DIALOG_ARGS)
        val selectedCountry = countryDataList.first { countryJsonData -> countryJsonData.countryId == countryId }
        return AlertDialog.Builder(activity)
                .setAdapter(ArrayAdapter<String>(activity, R.layout.simple_list_item_1, selectedCountry.levelOneValues?.map { it.value }), { dialog, position ->
                    mListener?.selectedLevel1(selectedCountry.levelOneValues?.get(position))
                })
                .create()
    }

    fun setOnLevel1SelectedListener(listener: OnLevel1SelectedListener) {
        mListener = listener
    }
}

interface OnLevel1SelectedListener {
    fun selectedLevel1(level1Value: LevelOneValuesItem?)
}
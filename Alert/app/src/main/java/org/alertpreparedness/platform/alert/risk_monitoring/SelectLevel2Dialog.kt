package org.alertpreparedness.platform.alert.risk_monitoring

import android.R
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.ArrayAdapter

/**
 * Created by Fei on 11/11/2017.
 */
class SelectLevel2Dialog: DialogFragment() {

    private var mListener: OnLevel2SelectedListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        @Suppress("UNCHECKED_CAST")
        val countryDataList = arguments.getSerializable(SelectAreaActivity.SELECT_DIALOG_ARGS) as ArrayList<CountryJsonData>
        val countryId = arguments.getInt(SelectAreaActivity.SELECT_LEVEL1_DIALOG_ARGS)
        val level1Id = arguments.getInt(SelectAreaActivity.SELECT_LEVEL2_DIALOG_ARGS)
        val selectedCountry = countryDataList.first { countryJsonData -> countryJsonData.countryId == countryId }
        val level2s = selectedCountry.levelOneValues?.first { it.id == level1Id }?.levelTwoValues
        return AlertDialog.Builder(activity)
                .setAdapter(ArrayAdapter<String>(activity, R.layout.simple_list_item_1, level2s?.map { it.value }), { dialog, position ->
                    mListener?.selectedLevel2(level2s?.get(position))
                })
                .create()
    }

    fun setOnLevel2SelectedListener(listener: OnLevel2SelectedListener) {
        mListener = listener
    }
}

interface OnLevel2SelectedListener {
    fun selectedLevel2(level2Value: LevelTwoValuesItem?)
}
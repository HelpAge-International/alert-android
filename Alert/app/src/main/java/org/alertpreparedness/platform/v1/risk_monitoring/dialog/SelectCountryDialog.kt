package org.alertpreparedness.platform.v1.risk_monitoring.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.ArrayAdapter
import org.alertpreparedness.platform.v1.risk_monitoring.model.CountryJsonData
import org.alertpreparedness.platform.v1.risk_monitoring.view.SelectAreaActivity
import org.alertpreparedness.platform.v1.utils.Constants

/**
 * Created by Fei on 11/11/2017.
 */
class SelectCountryDialog: DialogFragment() {

    private var mListener: OnCountrySelectedListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        @Suppress("UNCHECKED_CAST")
        val countryDataList = arguments!!.getSerializable(SelectAreaActivity.SELECT_DIALOG_ARGS) as ArrayList<CountryJsonData>
        return AlertDialog.Builder(activity)
                .setAdapter(ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, countryDataList.map { Constants.COUNTRIES[it.countryId.toInt()] }), { dialog, position ->
                    mListener?.selectedCountry(countryDataList[position])
                })
                .create()
    }

    fun setOnCountrySelectedListener(listener: OnCountrySelectedListener) {
        mListener = listener
    }
}

interface OnCountrySelectedListener {
    fun selectedCountry(countryJsonData: CountryJsonData)
}
package org.alertpreparedness.platform.alert.risk_monitoring

import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.widget.EditText
import org.alertpreparedness.platform.alert.R
import org.jetbrains.anko.find

/**
 * Created by fei on 08/11/2017.
 */
class SourceDialogFragment: DialogFragment() {

    private lateinit var etName:EditText
    private lateinit var etSource:EditText
    private var listener:SourceCreateListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        val view = View.inflate(activity, R.layout.dialog_indicator_source, null)
        initView(view)

        return AlertDialog.Builder(activity)
                .setView(view)
                .setTitle("Add source")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("SAVE", {_,_ ->
                    run {
//                        Timber.d("selected: %s, %s", etName.text, etSource.text)
                        val source = ModelSource(etName.text.toString(), etSource.text.toString())
                        listener?.getCreatedSource(source)
                    }
                })
                .create()
    }

    private fun initView(view: View) {
        etName = view.find(R.id.etSourceName)
        etSource = view.find(R.id.etSourceLink)
    }

    fun setOnSourceCreatedListener(sourceListener:SourceCreateListener) {
        listener = sourceListener
    }
}

interface SourceCreateListener {
    fun getCreatedSource(source:ModelSource)
}
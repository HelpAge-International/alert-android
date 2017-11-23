package org.alertpreparedness.platform.alert.risk_monitoring.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import es.dmoral.toasty.Toasty
import org.alertpreparedness.platform.alert.R
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelIndicator
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelSource
import org.jetbrains.anko.find
import timber.log.Timber

/**
 * Created by fei on 23/11/2017.
 */
class ShowInformationSourceDialog : DialogFragment() {

    private lateinit var mSources: List<ModelSource>
    private lateinit var mAdapter: InfoSourceAdapter
    private lateinit var mIndicatorModel: ModelIndicator

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mIndicatorModel = arguments[BottomSheetDialog.INDICATOR_MODEL] as ModelIndicator
        mSources = mIndicatorModel.source
        val view = View.inflate(activity, R.layout.show_information_source_item, null)
        mAdapter = InfoSourceAdapter(activity, mSources)
        return AlertDialog.Builder(activity)
                .setTitle("Information sources")
                .setAdapter(mAdapter, { _, position ->
                    Timber.d("position: %s", mSources[position].toString())
                    val clickedSource = mSources[position]
                    if (clickedSource.link != null && android.util.Patterns.WEB_URL.matcher(clickedSource.link).matches()) {
//                        browse(clickedSource.link, true)
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(clickedSource.link))
                        val packageManager = activity.packageManager
                        if (intent.resolveActivity(packageManager) != null) {
                            startActivity(intent)
                        } else {
                            Toasty.warning(activity, "No activity can handle browse web url", Toast.LENGTH_LONG).show()
                        }
                    }
                })
                .setNegativeButton("CLOSE", null)
                .create()
    }
}

class InfoSourceAdapter(context: Context, data: List<ModelSource>) : BaseAdapter() {

    private val mContext = context
    private val mData = data

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var convertView: View? = null
        var holder: ViewHolder? = null
        if (p1 == null) {
            convertView = View.inflate(mContext, R.layout.show_information_source_item, null)
            holder = ViewHolder(null, null)
            holder.title = convertView?.find(R.id.tvInformationSourceTitle)
            holder.link = convertView?.find(R.id.ivInformationSourceLink)
            convertView.tag = holder
        } else {
            convertView = p1
            holder = p1.tag as ViewHolder
        }
        val model = getItem(p0)
        holder.title?.text = model.name
        holder.link?.visibility = if (model.link != null && model.link.isNotEmpty() && android.util.Patterns.WEB_URL.matcher(model.link).matches()) View.VISIBLE else View.GONE
        return convertView!!
    }

    override fun getItem(p0: Int): ModelSource {
        return mData[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return mData.size
    }


    private class ViewHolder(var title: TextView?, var link: ImageView?)
}
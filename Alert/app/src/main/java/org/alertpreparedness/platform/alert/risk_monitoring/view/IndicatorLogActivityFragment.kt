package org.alertpreparedness.platform.alert.risk_monitoring.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_indicator_log.view.*
import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.R
import org.alertpreparedness.platform.alert.risk_monitoring.adapter.IndicatorLogRVAdapter
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelLog
import org.alertpreparedness.platform.alert.risk_monitoring.view_model.ActiveRiskViewModel
import org.alertpreparedness.platform.alert.utils.*
import org.joda.time.DateTime
import timber.log.Timber
import javax.inject.Inject

/**
 * A placeholder fragment containing a simple view.
 */
class IndicatorLogActivityFragment : Fragment() {

    private lateinit var mViewmodel: ActiveRiskViewModel
    private var mTriggerSelection: Int = 0
    private var mIndicatorId: String? = null

    @Inject
    lateinit var permissions : PermissionsHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_indicator_log, container, false)
        initViews(view)
        initData(view)
        initListeners(view)
        return view
    }

    private fun initViews(view: View) {
        view.rvIndicatorLog.layoutManager = LinearLayoutManager(activity)
        view.rvIndicatorLog.addItemDecoration(DividerItemDecoration(activity, RecyclerView.VERTICAL))
    }

    private fun initData(view: View) {
        mIndicatorId = activity.intent.getStringExtra(IndicatorLogActivity.INDICATOR_ID)
        mTriggerSelection = activity.intent.getIntExtra(IndicatorLogActivity.TRIGGER_SELECTION, 0)
        mViewmodel = ViewModelProviders.of(this).get(ActiveRiskViewModel::class.java)
        mIndicatorId?.apply {
            mViewmodel.getLiveIndicatorLogs(this).observe(this@IndicatorLogActivityFragment, Observer { logs ->
                logs?.let {
                    val sortedList = logs.sortedWith(Comparator({first,next ->
                         return@Comparator if (first.timeStamp > next.timeStamp) -1 else 1
                    }))
                    view.rvIndicatorLog.adapter = IndicatorLogRVAdapter(sortedList, activity, mIndicatorId as String, fragmentManager)
                }
            })
        }
    }

    private fun initListeners(view: View?) {
        view?.ivIndicatorLog?.setOnClickListener {
            if (!permissions.checkCreateNote()) {
                SnackbarHelper.show(activity, activity.getString(R.string.permission_note_create_error))
                return@setOnClickListener
            }
            if (view.etIndicatorLog.text.isEmpty()) {
                SnackbarHelper.show(activity, activity.getString(R.string.note_cannot_be_empty))
                return@setOnClickListener
            }
            mIndicatorId?.apply {
                val model = ModelLog(null, PreferHelper.getString(activity, Constants.UID), view.etIndicatorLog.text.toString(), DateTime().millis, mTriggerSelection)
                mViewmodel.addLogToIndicator(model, mIndicatorId as String)
            }
            AppUtils.hideSoftKeyboard(context, view.etIndicatorLog)
            view.etIndicatorLog.text.clear()
        }
    }
}

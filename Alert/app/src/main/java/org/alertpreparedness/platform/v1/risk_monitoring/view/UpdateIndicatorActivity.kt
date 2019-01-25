package org.alertpreparedness.platform.v1.risk_monitoring.view

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import es.dmoral.toasty.Toasty
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_update_indicator.*
import kotlinx.android.synthetic.main.content_update_indicator.*
import org.alertpreparedness.platform.v1.BaseActivity
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v1.risk_monitoring.model.ModelIndicator
import org.alertpreparedness.platform.v1.risk_monitoring.model.ModelLog
import org.alertpreparedness.platform.v1.risk_monitoring.view_model.ActiveRiskViewModel
import org.alertpreparedness.platform.v1.utils.Constants
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton
import org.joda.time.DateTime
import java.util.concurrent.TimeUnit

class UpdateIndicatorActivity : BaseActivity() {

    private lateinit var mViewModel: ActiveRiskViewModel
    private var mHazardId: String? = null
    private var mIndicatorId: String? = null
    private lateinit var mIndicatorModel: ModelIndicator

    companion object {
        fun startActivity(context: Context, hazardId: String, indicatorId: String) {
            val i = Intent(context, UpdateIndicatorActivity::class.java)
            i.putExtra(ActiveRiskFragment.HAZARD_ID, hazardId)
            i.putExtra(ActiveRiskFragment.INDICATOR_ID, indicatorId)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(i)
        }

        val GREEN_TRIGGER_POSITION = 0
        val AMBER_TRIGGER_POSITION = 1
        val RED_TRIGGER_POSITION = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_indicator)
        initViews()
        initData()
        initListeners()
    }

    private fun initData() {
        mViewModel = ViewModelProviders.of(this).get(ActiveRiskViewModel::class.java)
        mHazardId = intent.getStringExtra(ActiveRiskFragment.HAZARD_ID)
        mIndicatorId = intent.getStringExtra(ActiveRiskFragment.INDICATOR_ID)
        if (mHazardId != null && mIndicatorId != null) {
            mViewModel.getLiveIndicatorModel(mHazardId as String, mIndicatorId as String).observe(this, Observer { model ->
                model?.let { mIndicatorModel = model }
                tvUpdateIndicatorName.text = model?.name ?: ""
                val modelTriggerGreen = model?.trigger?.get(GREEN_TRIGGER_POSITION)
                modelTriggerGreen?.let { tvUpdateIndicatorDescGreen.text = String.format("Check every %s %s", modelTriggerGreen.frequencyValue, Constants.FREQUENCY_NAMES[modelTriggerGreen.durationType.toInt()]) }
                val modelTriggerAmber = model?.trigger?.get(AMBER_TRIGGER_POSITION)
                modelTriggerAmber?.let { tvUpdateIndicatorDescAmber.text = String.format("Check every %s %s", modelTriggerAmber.frequencyValue, Constants.FREQUENCY_NAMES[modelTriggerAmber.durationType.toInt()]) }
                val modelTriggerRed = model?.trigger?.get(RED_TRIGGER_POSITION)
                modelTriggerRed?.let { tvUpdateIndicatorDescRed.text = String.format("Check every %s %s", modelTriggerRed.frequencyValue, Constants.FREQUENCY_NAMES[modelTriggerRed.durationType.toInt()]) }
            })
        }
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        supportActionBar?.title = ""
    }

    private fun initListeners() {
        tvUpdateIndicatorLevelGreen.setOnClickListener {
//            if (mIndicatorModel.triggerLevel == GREEN_TRIGGER_POSITION) {
//                Toasty.info(this, "Already in Green level").show()
//                return@setOnClickListener
//            }
            alert("Are you sure you want to update to Green?", "Update Level",
                    {
                        yesButton { updateIndicatorLevelTo(GREEN_TRIGGER_POSITION) }
                        noButton { }
                    }
            ).show()
        }

        tvUpdateIndicatorLevelAmber.setOnClickListener {
//            if (mIndicatorModel.triggerLevel == AMBER_TRIGGER_POSITION) {
//                Toasty.info(this, "Already in Amber level").show()
//                return@setOnClickListener
//            }
            alert("Are you sure you want to update to Amber?", "Update Level",
                    {
                        yesButton { updateIndicatorLevelTo(AMBER_TRIGGER_POSITION) }
                        noButton { }
                    }
            ).show()
        }

        tvUpdateIndicatorLevelRed.setOnClickListener {
//            if (mIndicatorModel.triggerLevel == RED_TRIGGER_POSITION) {
//                Toasty.info(this, "Already in Red level").show()
//                return@setOnClickListener
//            }
            alert("Are you sure you want to update to Red?", "Update Level",
                    {
                        yesButton { updateIndicatorLevelTo(RED_TRIGGER_POSITION) }
                        noButton { }
                    }
            ).show()
        }
    }

    private fun updateIndicatorLevelTo(level: Int) {
        if (mHazardId != null && mIndicatorId != null) {
            mViewModel.updateIndicatorLevel(mHazardId as String, mIndicatorId as String, mIndicatorModel, level)
            val log = ModelLog(null, uid, when (level) {
                GREEN_TRIGGER_POSITION -> {
                    "Indicator level was updated to GREEN"
                }
                AMBER_TRIGGER_POSITION -> {
                    "Indicator level was updated to AMBER"
                }
                else -> {
                    "Indicator level was updated to RED"
                }
            }, DateTime.now().millis, level, null)
            mViewModel.addLogToIndicator(log, mIndicatorId as String)
            Toasty.success(this, "Indicator level was updated successfully").show()
            Observable.timer(Constants.MENU_CLOSING_DURATION, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe({
                onBackPressed()
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}

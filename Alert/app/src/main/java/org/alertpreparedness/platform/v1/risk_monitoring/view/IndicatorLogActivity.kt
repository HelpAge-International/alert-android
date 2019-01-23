package org.alertpreparedness.platform.v1.risk_monitoring.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_indicator_log.*
import org.alertpreparedness.platform.v1.BaseActivity
import org.alertpreparedness.platform.v1.R

class IndicatorLogActivity : BaseActivity() {

    companion object {
        val TRIGGER_SELECTION = "trigger_selection"
        val INDICATOR_ID = "indicator_id"
        fun startActivity(context: Context, indicatorId:String, triggerSelection:Int) {
            val i = Intent(context, IndicatorLogActivity::class.java)
            i.putExtra(INDICATOR_ID, indicatorId)
            i.putExtra(TRIGGER_SELECTION, triggerSelection)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_indicator_log)
        initViews()
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.indicator_log)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

}

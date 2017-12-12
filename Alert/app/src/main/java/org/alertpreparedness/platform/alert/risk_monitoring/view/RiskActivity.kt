package org.alertpreparedness.platform.alert.risk_monitoring.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_risk.*
import kotlinx.android.synthetic.main.content_risk.*
import org.alertpreparedness.platform.alert.MainDrawer
import org.alertpreparedness.platform.alert.R
import org.alertpreparedness.platform.alert.dashboard.activity.CreateAlertActivity
import org.alertpreparedness.platform.alert.risk_monitoring.adapter.RiskPagerAdapter
import org.alertpreparedness.platform.alert.utils.Constants
import timber.log.Timber
import java.util.concurrent.TimeUnit

class RiskActivity : MainDrawer() {

    companion object RiskIntent {
        fun getIntent(context: Context): Intent {
            return Intent(context, RiskActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateDrawer(R.layout.activity_risk)
        initView()
        initListeners()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        val supportActionBar = supportActionBar
        supportActionBar?.title = getString(R.string.risk_monitoring)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)

        fabRiskMenu.setClosedOnTouchOutside(true)

        tlRisk.addTab(tlRisk.newTab())
        tlRisk.addTab(tlRisk.newTab())
        tlRisk.setupWithViewPager(vpRisk)

        val pagerAdapter = RiskPagerAdapter(supportFragmentManager, tlRisk.tabCount)
        vpRisk.adapter = pagerAdapter
    }

    private fun initListeners() {
        fabRiskIndicator.setOnClickListener({
            Timber.d("create indicator")
            fabRiskMenu.close(true)
            Observable.timer(Constants.MENU_CLOSING_DURATION, TimeUnit.MILLISECONDS).subscribe {
                AddIndicatorActivity.startActivity(this@RiskActivity)
            }
        })
        fabRiskAlert.setOnClickListener({
            fabRiskMenu.close(true)
            Observable.timer(Constants.MENU_CLOSING_DURATION, TimeUnit.MILLISECONDS).subscribe {
                startActivity(Intent(this, CreateAlertActivity::class.java))
            }
        })


    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.notification_menu, menu)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onBackPressed() {
        clearAllActivities()
    }

}

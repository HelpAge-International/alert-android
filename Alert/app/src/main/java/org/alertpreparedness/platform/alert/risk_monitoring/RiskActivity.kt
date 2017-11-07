package org.alertpreparedness.platform.alert.risk_monitoring

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import kotlinx.android.synthetic.main.activity_risk.*
import kotlinx.android.synthetic.main.content_risk.*
import org.alertpreparedness.platform.alert.BaseActivity
import org.alertpreparedness.platform.alert.R
import timber.log.Timber

class RiskActivity : BaseActivity() {

    companion object RiskIntent {
        fun getIntent(context: Context): Intent {
            return Intent(context, RiskActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_risk)
        initView()
        initListeners()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
//        val supportActionBar = supportActionBar
//        supportActionBar?.title = getString(R.string.risk_monitoring)


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
        })
        fabRiskAlert.setOnClickListener({
            Timber.d("create alert")
            fabRiskMenu.close(true)
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.notification_menu, menu)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onBackPressed() {
        clearAllActivites()
    }

}

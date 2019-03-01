package org.alertpreparedness.platform.v1.risk_monitoring.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_risk_monitoring.*
import kotlinx.android.synthetic.main.content_risk.*
import org.alertpreparedness.platform.v1.MainDrawer
import org.alertpreparedness.platform.R
import org.alertpreparedness.platform.v1.dagger.DependencyInjector
import org.alertpreparedness.platform.v1.dashboard.activity.CreateAlertActivity
import org.alertpreparedness.platform.v1.risk_monitoring.adapter.RiskPagerAdapter
import org.alertpreparedness.platform.v1.utils.Constants
import org.alertpreparedness.platform.v1.utils.PermissionsHelper
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RiskFragment : Fragment() {

    @Inject
    lateinit var permissions : PermissionsHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        DependencyInjector.userScopeComponent().inject(this)

        val v = inflater?.inflate(R.layout.fragment_risk_monitoring, container, false)

        (activity as MainDrawer).toggleActionBarWithTitle(MainDrawer.ActionBarState.NORMAL, R.string.risk_monitoring)
        (activity as MainDrawer).removeActionbarElevation()

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListeners()

        println("permissions = ${permissions}")

        if(!permissions.checkCreateIndicator()) {
            fabRiskMenu.removeMenuButton(fabRiskIndicator);
        }
    }

    private fun initView() {
        fabRiskMenu.setClosedOnTouchOutside(true)

        tlRisk.addTab(tlRisk.newTab())
        tlRisk.addTab(tlRisk.newTab())
        tlRisk.setupWithViewPager(vpRisk)

        val pagerAdapter = RiskPagerAdapter(activity!!.supportFragmentManager, tlRisk.tabCount, activity!!)
        vpRisk.adapter = pagerAdapter
    }


    private fun initListeners() {

        fabRiskAlert.setOnClickListener({
            fabRiskMenu.close(true)
            Observable.timer(Constants.MENU_CLOSING_DURATION, TimeUnit.MILLISECONDS).subscribe {
                startActivity(Intent(context, CreateAlertActivity::class.java))
            }
        })

        fabRiskIndicator.setOnClickListener({
            fabRiskMenu.close(true)
            Observable.timer(Constants.MENU_CLOSING_DURATION, TimeUnit.MILLISECONDS).subscribe {
                startActivity(Intent(context, AddIndicatorActivity::class.java))
            }
        })


    }

//    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.notification_menu, menu)
//        return super.onPrepareOptionsMenu(menu)
//    }
//
//    override fun onBackPressed() {
//        clearAllActivities()
//    }

}

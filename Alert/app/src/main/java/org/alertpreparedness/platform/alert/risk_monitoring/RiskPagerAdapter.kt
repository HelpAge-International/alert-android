package org.alertpreparedness.platform.alert.risk_monitoring

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.R

/**
 * Created by fei on 07/11/2017.
 */
class RiskPagerAdapter(fm: FragmentManager, tabs: Int) : FragmentStatePagerAdapter(fm) {
    private var totalTabs = tabs

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                ActiveRiskFragment()
            }
            else -> ArchivedRiskFragment()
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> {
                AlertApplication.getContext().getString(R.string.active)
            }
            else -> {
                AlertApplication.getContext().getString(R.string.archived)
            }
        }
    }
}
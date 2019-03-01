package org.alertpreparedness.platform.v1.risk_monitoring.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import org.alertpreparedness.platform.R
import org.alertpreparedness.platform.v1.risk_monitoring.view.ActiveRiskFragment
import org.alertpreparedness.platform.v1.risk_monitoring.view.ArchivedRiskFragment

/**
 * Created by fei on 07/11/2017.
 */
class RiskPagerAdapter(fm: FragmentManager, tabs: Int, private val context : Context) : FragmentStatePagerAdapter(fm) {
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
                context.getString(R.string.active)
            }
            else -> {
                context.getString(R.string.archived)
            }
        }
    }
}
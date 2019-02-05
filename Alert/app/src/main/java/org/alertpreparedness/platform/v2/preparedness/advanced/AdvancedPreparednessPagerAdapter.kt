package org.alertpreparedness.platform.v2.preparedness.advanced

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class AdvancedPreparednessPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getCount(): Int {
        return 6
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> InProgressAdvancedPreparednessFragment()
            1 -> ExpiredAdvancedPreparednessFragment()
            2 -> UnassignedAdvancedPreparednessFragment()
            3 -> CompleteAdvancedPreparednessFragment()
            4 -> InActiveAdvancedPreparednessFragment()
            5 -> ArchivedAdvancedPreparednessFragment()
            else -> InProgressAdvancedPreparednessFragment()
        }
    }
}
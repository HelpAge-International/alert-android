package org.alertpreparedness.platform.v2.preparedness.minimum

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MinimumPreparednessPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getCount(): Int {
        return 5
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> InProgressMinimumPreparednessFragment()
            1 -> ExpiredMinimumPreparednessFragment()
            2 -> UnassignedMinimumPreparednessFragment()
            3 -> CompleteMinimumPreparednessFragment()
            4 -> ArchivedMinimumPreparednessFragment()
            else -> InProgressMinimumPreparednessFragment()
        }
    }
}
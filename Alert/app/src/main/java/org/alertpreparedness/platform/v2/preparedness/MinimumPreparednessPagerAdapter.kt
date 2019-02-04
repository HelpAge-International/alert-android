package org.alertpreparedness.platform.v2.preparedness

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import org.alertpreparedness.platform.v2.preparedness.minimum.ArchivedMinimumPreparednessFragment
import org.alertpreparedness.platform.v2.preparedness.minimum.CompleteMinimumPreparednessFragment
import org.alertpreparedness.platform.v2.preparedness.minimum.ExpiredMinimumPreparednessFragment
import org.alertpreparedness.platform.v2.preparedness.minimum.InProgressMinimumPreparednessFragment
import org.alertpreparedness.platform.v2.preparedness.minimum.UnassignedMinimumPreparednessFragment

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
package org.alertpreparedness.platform.v1.risk_monitoring.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.alertpreparedness.platform.R

/**
 * A placeholder fragment containing a simple view.
 */
class LogIndicatorActivityFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_log_indicator, container, false)
    }
}

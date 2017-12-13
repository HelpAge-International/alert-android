package org.alertpreparedness.platform.alert.risk_monitoring.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.alertpreparedness.platform.alert.R

/**
 * A placeholder fragment containing a simple view.
 */
class LogIndicatorActivityFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_log_indicator, container, false)
    }
}

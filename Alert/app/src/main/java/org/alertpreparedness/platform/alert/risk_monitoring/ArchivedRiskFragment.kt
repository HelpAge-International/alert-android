package org.alertpreparedness.platform.alert.risk_monitoring


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import org.alertpreparedness.platform.alert.R


/**
 * A simple [Fragment] subclass.
 */
class ArchivedRiskFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_archived_risk, container, false)
    }

}// Required empty public constructor

package org.alertpreparedness.platform.alert.risk_monitoring.view

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_log_indicator.*
import org.alertpreparedness.platform.alert.BaseActivity
import org.alertpreparedness.platform.alert.R

class LogIndicatorActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_indicator)
        setSupportActionBar(toolbar)
    }

}

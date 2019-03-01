package org.alertpreparedness.platform.v1.risk_monitoring.view

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_log_indicator.*
import org.alertpreparedness.platform.v1.BaseActivity
import org.alertpreparedness.platform.R

class LogIndicatorActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_indicator)
        setSupportActionBar(toolbar)
    }

}

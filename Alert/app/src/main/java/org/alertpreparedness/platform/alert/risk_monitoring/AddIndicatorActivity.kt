package org.alertpreparedness.platform.alert.risk_monitoring

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import kotlinx.android.synthetic.main.activity_add_indicator.*
import kotlinx.android.synthetic.main.content_add_indicator.*
import org.alertpreparedness.platform.alert.R
import timber.log.Timber

class AddIndicatorActivity : AppCompatActivity() {

    private lateinit var mPopupMenu: PopupMenu
    private lateinit var mPopupMenuFrequencyGreen: PopupMenu
    private lateinit var mPopupMenuFrequencyAmber: PopupMenu
    private lateinit var mPopupMenuFrequencyRed: PopupMenu

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, AddIndicatorActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_indicator)
        initData()
        initViews()
        initListeners()
    }

    private fun initData() {
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        val supportActionBar = supportActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_indicator)
        supportActionBar?.setHomeAsUpIndicator(android.R.drawable.ic_delete)

        mPopupMenu = PopupMenu(this, tvSelectHazard)
        mPopupMenu.menu.add("menu 1")
        mPopupMenu.menu.add("menu 2")
        mPopupMenu.menuInflater.inflate(R.menu.popup_template_menu, mPopupMenu.menu)

        mPopupMenuFrequencyGreen = PopupMenu(this, tvGreenFrequency)
        mPopupMenuFrequencyGreen.menuInflater.inflate(R.menu.popup_menu_frequency, mPopupMenuFrequencyGreen.menu)

        mPopupMenuFrequencyAmber = PopupMenu(this, tvAmberFrequency)
        mPopupMenuFrequencyAmber.menuInflater.inflate(R.menu.popup_menu_frequency, mPopupMenuFrequencyAmber.menu)

        mPopupMenuFrequencyRed = PopupMenu(this, tvRedFrequency)
        mPopupMenuFrequencyRed.menuInflater.inflate(R.menu.popup_menu_frequency, mPopupMenuFrequencyRed.menu)
    }

    private fun initListeners() {
        tvSelectHazard.setOnClickListener {
            Timber.d("show popup menu")
            mPopupMenu.show()
        }

        mPopupMenu.setOnMenuItemClickListener { p0 ->
            when (p0?.title) {
                "menu 1" -> {
                    Timber.d("1 clicked")
                    true
                }
                else -> {
                    Timber.d("2 clicked")
                    true
                }
            }
        }

        tvIndicatorAddSource.setOnClickListener {
            SourceDialogFragment().show(supportFragmentManager, "dialog_source")
        }

        tvGreenFrequency.setOnClickListener {
            mPopupMenuFrequencyGreen.show()
        }

        tvAmberFrequency.setOnClickListener {
            mPopupMenuFrequencyAmber.show()
        }

        tvRedFrequency.setOnClickListener {
            mPopupMenuFrequencyRed.show()
        }

        tvAssignTo.setOnClickListener {
            AssignToDialogFragment().show(supportFragmentManager, "dialog_assign")
        }

        llIndicatorSelectLocation.setOnClickListener {
            LocationSelectionDialogFragment().show(supportFragmentManager, "dialog_location_selection")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_indicator_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menuAddIndicator -> {
                Timber.d("save clicked!")
            }
            else -> {
                Timber.d("noting clicked")
            }
        }
        return super.onOptionsItemSelected(item)
    }

}

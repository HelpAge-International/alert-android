package org.alertpreparedness.platform.alert.risk_monitoring

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import kotlinx.android.synthetic.main.activity_add_indicator.*
import kotlinx.android.synthetic.main.content_add_indicator.*
import org.alertpreparedness.platform.alert.R
import org.alertpreparedness.platform.alert.utils.Constants
import timber.log.Timber

class AddIndicatorActivity : AppCompatActivity(), OnSourceDeleteListener {

    private lateinit var mPopupMenu: PopupMenu
    private lateinit var mPopupMenuFrequencyGreen: PopupMenu
    private lateinit var mPopupMenuFrequencyAmber: PopupMenu
    private lateinit var mPopupMenuFrequencyRed: PopupMenu
    private lateinit var mDialogSource: SourceDialogFragment
    private lateinit var mSources: MutableList<ModelSource>
    private lateinit var mSourceAdapter: SourceRVAdapter
    private lateinit var mDialogAssign: AssignToDialogFragment
    private lateinit var mDialogLocation: LocationSelectionDialogFragment
    private var mSelectedAssignPosition = 0
    private var mSelectedLocation = 0

    private val mIndicatorModel = ModelIndicator()
    private lateinit var mViewModel: AddIndicatorViewModel
    private var mHazards: List<ModelHazard>? = null
    private var mIsCountryContext = false
    private var mStaff: List<ModelUserPublic>? = null

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, AddIndicatorActivity::class.java)
            context.startActivity(intent)
        }

        val SELECTED_LOCATION = "selected_location"
        val STAFF_SELECTION = "staff_selection"
        val ASSIGN_POSITION = "assign_position"
        val LOCATION_LIST = listOf<String>("National", "Subnational", "Use my location")
        val AREA_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_indicator)
        mViewModel = ViewModelProviders.of(this).get(AddIndicatorViewModel::class.java)
        initData()
        initViews()
        initListeners()
    }


    private fun initData() {
        mSources = mutableListOf()
        mViewModel.getStaffLive().observe(this, Observer { users ->
            mStaff = users
        })
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        val supportActionBar = supportActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_indicator)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)

        rvSources.hasFixedSize()
        rvSources.layoutManager = LinearLayoutManager(this)
        mSourceAdapter = SourceRVAdapter(mSources)
        rvSources.adapter = mSourceAdapter
        mSourceAdapter.setOnSourceDeleteListener(this)

        mPopupMenu = PopupMenu(this, tvSelectHazard)
        mPopupMenu.menu.add("Country Context")
        mViewModel.getHazardsLive().observe(this, Observer<List<ModelHazard>> {
            mHazards = it
            mHazards?.forEach { mPopupMenu.menu.add(Constants.HAZARD_SCENARIO_NAME[it.hazardScenario]) }
        })
        mPopupMenu.menuInflater.inflate(R.menu.popup_template_menu, mPopupMenu.menu)

        mPopupMenuFrequencyGreen = PopupMenu(this, tvGreenFrequency)
        mPopupMenuFrequencyGreen.menuInflater.inflate(R.menu.popup_menu_frequency, mPopupMenuFrequencyGreen.menu)

        mPopupMenuFrequencyAmber = PopupMenu(this, tvAmberFrequency)
        mPopupMenuFrequencyAmber.menuInflater.inflate(R.menu.popup_menu_frequency, mPopupMenuFrequencyAmber.menu)

        mPopupMenuFrequencyRed = PopupMenu(this, tvRedFrequency)
        mPopupMenuFrequencyRed.menuInflater.inflate(R.menu.popup_menu_frequency, mPopupMenuFrequencyRed.menu)

        mDialogSource = SourceDialogFragment()
        mDialogAssign = AssignToDialogFragment()
        mDialogLocation = LocationSelectionDialogFragment()
    }

    private fun initListeners() {
        tvSelectHazard.setOnClickListener {
            Timber.d("show popup menu")
            mPopupMenu.show()
        }

        mPopupMenu.setOnMenuItemClickListener { p0 ->
            Timber.d("menu: %s", p0?.title)
            tvSelectHazard.text = p0?.title
            //TODO NEED UPDATE THIS WHEN SUBMIT
            if (p0?.title?.equals("Country Context") == true) {
                mIsCountryContext = true
            } else {
                mIsCountryContext = false
                mIndicatorModel.hazardScenario = mHazards?.get(mHazards!!.map { Constants.HAZARD_SCENARIO_NAME[it.hazardScenario] }.indexOf(p0?.title)) ?: ModelHazard()
            }
            Timber.d(mIndicatorModel.toString())
            true
        }

        tvIndicatorAddSource.setOnClickListener {
            mDialogSource.show(supportFragmentManager, "dialog_source")
        }

        mDialogSource.setOnSourceCreatedListener(object : SourceCreateListener {
            override fun getCreatedSource(source: ModelSource) {
                Timber.d("source: %s", source)
                mSources.add(source)
                mSourceAdapter.notifyItemInserted(mSources.size - 1)
            }
        })

        tvGreenFrequency.setOnClickListener {
            mPopupMenuFrequencyGreen.show()
        }

        mPopupMenuFrequencyGreen.setOnMenuItemClickListener { menuItem ->
            tvGreenFrequency.text = menuItem.title
            true
        }

        tvAmberFrequency.setOnClickListener {
            mPopupMenuFrequencyAmber.show()
        }

        mPopupMenuFrequencyAmber.setOnMenuItemClickListener { menuItem ->
            tvAmberFrequency.text = menuItem.title
            true
        }

        tvRedFrequency.setOnClickListener {
            mPopupMenuFrequencyRed.show()
        }

        mPopupMenuFrequencyRed.setOnMenuItemClickListener { menuItem ->
            tvRedFrequency.text = menuItem.title
            true
        }

        tvAssignTo.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(ASSIGN_POSITION, mSelectedAssignPosition)
            mDialogAssign.arguments = bundle
            mDialogAssign.show(supportFragmentManager, "dialog_assign")
        }

        mDialogAssign.setOnAssignToListener(object : AssignToListener {
            override fun userAssignedTo(userId: String, position: Int) {
                tvAssignTo.text = userId
                mSelectedAssignPosition = position
            }
        })

        llIndicatorSelectLocation.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(SELECTED_LOCATION, mSelectedLocation)
//            bundle.putInt(STAFF_SELECTION, mStaff)
            mDialogLocation.arguments = bundle
            mDialogLocation.show(supportFragmentManager, "dialog_location_selection")
        }

        mDialogLocation.setOnLocationSelectedListener(object : OnLocationSelected {
            override fun locationSelected(location: Int) {
                tvIndicatorLocation.text = LOCATION_LIST[location]
                mSelectedLocation = location
                when (location) {
                    0 -> {
                        tvIndicatorSelectSubNational.visibility = View.GONE
                        rvLocationSubNational.visibility = View.GONE
                        tvIndicatorMyLocation.visibility = View.GONE
                    }
                    1 -> {
                        tvIndicatorSelectSubNational.visibility = View.VISIBLE
                        rvLocationSubNational.visibility = View.VISIBLE
                        tvIndicatorMyLocation.visibility = View.GONE
                    }
                    else -> {
                        tvIndicatorSelectSubNational.visibility = View.GONE
                        rvLocationSubNational.visibility = View.GONE
                        tvIndicatorMyLocation.visibility = View.VISIBLE
                    }
                }
            }
        })

        tvIndicatorSelectSubNational.setOnClickListener {
            startActivityForResult(Intent(this, SelectAreaActivity::class.java), AREA_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AREA_REQUEST_CODE -> {
                Timber.d("returned from select area")
            }
            else -> {
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_indicator_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.menuSave -> {
                Timber.d("save clicked!")
            }
            else -> {
                Timber.d("noting clicked")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun sourceRemovePosition(position: Int) {
        mSources.removeAt(position)
        mSourceAdapter.notifyItemRemoved(position)
    }

}

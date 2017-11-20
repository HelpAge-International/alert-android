package org.alertpreparedness.platform.alert.risk_monitoring.view

import android.annotation.SuppressLint
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
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.*
import com.tbruyelle.rxpermissions2.RxPermissions
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_add_indicator.*
import kotlinx.android.synthetic.main.content_add_indicator.*
import org.alertpreparedness.platform.alert.R
import org.alertpreparedness.platform.alert.risk_monitoring.adapter.AreaRVAdapter
import org.alertpreparedness.platform.alert.risk_monitoring.adapter.OnAreaDeleteListener
import org.alertpreparedness.platform.alert.risk_monitoring.adapter.OnSourceDeleteListener
import org.alertpreparedness.platform.alert.risk_monitoring.adapter.SourceRVAdapter
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.*
import org.alertpreparedness.platform.alert.risk_monitoring.model.*
import org.alertpreparedness.platform.alert.risk_monitoring.view_model.AddIndicatorViewModel
import org.alertpreparedness.platform.alert.utils.Constants
import org.joda.time.DateTime
import timber.log.Timber

class AddIndicatorActivity : AppCompatActivity(), OnSourceDeleteListener, OnAreaDeleteListener {

    private lateinit var mPopupMenu: PopupMenu
    private lateinit var mPopupMenuFrequencyGreen: PopupMenu
    private lateinit var mPopupMenuFrequencyAmber: PopupMenu
    private lateinit var mPopupMenuFrequencyRed: PopupMenu
    private lateinit var mDialogSource: SourceDialogFragment
    private lateinit var mSources: MutableList<ModelSource>
    private lateinit var mAreas: MutableList<ModelIndicatorLocation>
    private lateinit var mSourceAdapter: SourceRVAdapter
    private lateinit var mAreaAdapter: AreaRVAdapter
    private lateinit var mDialogAssign: AssignToDialogFragment
    private lateinit var mDialogLocation: LocationSelectionDialogFragment
    private var mSelectedAssignPosition = 0
    private var mSelectedLocation = 0

    private val mIndicatorModel = ModelIndicator()
    private lateinit var mViewModel: AddIndicatorViewModel
    private var mHazards: List<ModelHazard>? = null
    private var mIsCountryContext = false
    private var mStaff: ArrayList<ModelUserPublic>? = null

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationCallback: LocationCallback

    private val mHazardOtherNamesMap = mutableMapOf<String, String>()
    private var mCountryJsonList: List<CountryJsonData> = listOf()

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, AddIndicatorActivity::class.java)
            context.startActivity(intent)
        }

        val SELECTED_LOCATION = "selected_location"
        val STAFF_SELECTION = "staff_selection"
        val ASSIGN_POSITION = "assign_position"
        val COUNTRY_JSON_DATA = "country_json_data"
        val LOCATION_LIST = listOf<String>("National", "Subnational", "Use my location")
        val NATIONAL = 0
        val SUBNATIONAL = 1
        val USER_MY_LOCATION = 2
        val TRIGGER_FREQUENCY_LIST = listOf<String>("Hours", "Days", "Weeks", "Months")
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

    override fun onResume() {
        super.onResume()
        val available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        if (available != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, available, 0).show()
        } else {
            Timber.d("Google Api is ready")
        }
    }

    override fun onPause() {
        super.onPause()
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
    }


    private fun initData() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSources = mutableListOf()
        mAreas = mutableListOf()
        mViewModel.getStaffLive().observe(this, Observer { users ->
            mStaff = ArrayList(users)
        })
        mViewModel.getCountryJsonDataLive().observe(this, Observer { countryList ->
            countryList?.let {
                mCountryJsonList = countryList
                if (countryList.size == 248) {
                    mAreaAdapter = AreaRVAdapter(mAreas, countryList)
                    rvLocationSubNational.adapter = mAreaAdapter
                    mAreaAdapter.setOnAreaDeleteListener(this)
                }
            }
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

        rvLocationSubNational.hasFixedSize()
        rvLocationSubNational.layoutManager = LinearLayoutManager(this)
        mAreaAdapter = AreaRVAdapter(mAreas, mCountryJsonList)
        rvLocationSubNational.adapter = mAreaAdapter
        mAreaAdapter.setOnAreaDeleteListener(this)


        mPopupMenu = PopupMenu(this, tvSelectHazard)
        mPopupMenu.menu.add("Country Context")
        mViewModel.getHazardsLive().observe(this, Observer<List<ModelHazard>> {
            mHazards = it
            mHazards?.forEach {
                when (it.hazardScenario) {
                    -1 -> {
                        mViewModel.getHazardOtherNameMapLive(it).observe(this, Observer { pair ->
                            pair?.first?.let { mHazardOtherNamesMap[pair.first] = pair.second }
                            mPopupMenu.menu.add(pair?.second)
                        })
                    }
                    else -> {
                        mPopupMenu.menu.add(Constants.HAZARD_SCENARIO_NAME[it.hazardScenario])
                    }
                }
            }
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
                if (Constants.HAZARD_SCENARIO_NAME.contains(p0?.title)) {
                    mIndicatorModel.hazardScenario = mHazards?.filter { it.hazardScenario >= 0 }?.map { Constants.HAZARD_SCENARIO_NAME[it.hazardScenario] }?.indexOf(p0?.title)?.let { mHazards?.get(it) } ?: ModelHazard()
                } else {
                    Timber.d("other hazard name")
                    val customHazards = mHazards?.filter { it.hazardScenario == -1 }
                    Timber.d("custom size: %s", customHazards?.size)
                    Timber.d(mHazardOtherNamesMap.toString())
                    mIndicatorModel.hazardScenario = customHazards?.map { mHazardOtherNamesMap[it.id] }?.indexOf(p0?.title)?.let { customHazards[it] } ?: ModelHazard()
                }
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
            mStaff?.let { bundle.putSerializable(STAFF_SELECTION, mStaff) }
            mDialogAssign.arguments = bundle
            mDialogAssign.show(supportFragmentManager, "dialog_assign")
        }

        mDialogAssign.setOnAssignToListener(object : AssignToListener {
            override fun userAssignedTo(user: ModelUserPublic?, position: Int) {
                user?.let { tvAssignTo.text = String.format("%s %s", user.firstName, user.lastName) }
                mSelectedAssignPosition = position
                mIndicatorModel.assignee = user?.id
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
                mIndicatorModel.geoLocation = location
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
                        getLocation()
                    }
                }
            }
        })

        tvIndicatorSelectSubNational.setOnClickListener {
            startActivityForResult(Intent(this, SelectAreaActivity::class.java), AREA_REQUEST_CODE)
        }

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                val location = p0?.lastLocation
                tvIndicatorMyLocation.text = String.format("(%s,%s)", location?.latitude, location?.longitude)
                mIndicatorModel.gps = ModelGps(latitude = location?.latitude.toString(), longitude = location?.longitude.toString())
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val permission = RxPermissions(this)
        permission
                .request(android.Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe({ granted ->
                    if (granted) {
                        mFusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            if (location != null) {
                                Timber.d("location: %s / %s", location.longitude, location.latitude)
                                tvIndicatorMyLocation.text = String.format("(%s,%s)", location.latitude, location.longitude)
                                mIndicatorModel.gps = ModelGps(latitude = location.latitude.toString(), longitude = location.longitude.toString())
                            } else {
                                Timber.d("no location cached, need to request new")
                                val locationRequest = LocationRequest()
                                locationRequest.interval = 10000
                                locationRequest.fastestInterval = 5000
                                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                                val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
                                val client = LocationServices.getSettingsClient(this)
                                val task = client.checkLocationSettings(builder.build())
                                task.addOnSuccessListener {
                                    mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null)
                                }
                                task.addOnFailureListener(this, {
                                    Timber.d("location settings failed!!!")
                                })
                            }
                        }
                    } else {
                        Timber.d("Permission request denied")
                    }
                })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AREA_REQUEST_CODE -> {
                Timber.d("returned from select area")
                val area = data?.getParcelableExtra<ModelIndicatorLocation>(SelectAreaActivity.SELECTED_AREA)
                Timber.d(area?.toString())
                area?.let {
                    mAreas.add(area)
                    mIndicatorModel.affectedLocation = mAreas
                    mAreaAdapter.notifyItemInserted(mAreas.size - 1)
                }
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
                saveIndicator()
            }
            else -> {
                Timber.d("noting clicked")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveIndicator() {
        mIndicatorModel.source = mSources
        val triggerGreen = ModelTrigger(if (TRIGGER_FREQUENCY_LIST.contains(tvGreenFrequency.text.toString())) TRIGGER_FREQUENCY_LIST.indexOf(tvGreenFrequency.text.toString()).toString() else "",
                if (etIndicatorGreenValue.text.toString().isNotEmpty()) etIndicatorGreenValue.text.toString().toInt() else -1,
                tvIndicatorGreenName.text.toString())
        if (!triggerGreen.validateModel()) {
            Toasty.error(this, "Green trigger is not valid, please double check!").show()
            return
        }
        val triggerAmber = ModelTrigger(if (TRIGGER_FREQUENCY_LIST.contains(tvAmberFrequency.text.toString())) TRIGGER_FREQUENCY_LIST.indexOf(tvAmberFrequency.text.toString()).toString() else "",
                if (etIndicatorAmberValue.text.toString().isNotEmpty()) etIndicatorAmberValue.text.toString().toInt() else -1,
                tvIndicatorAmberName.text.toString())
        if (!triggerAmber.validateModel()) {
            Toasty.error(this, "Amber trigger is not valid, please double check!").show()
            return
        }
        val triggerRed = ModelTrigger(if (TRIGGER_FREQUENCY_LIST.contains(tvRedFrequency.text.toString())) TRIGGER_FREQUENCY_LIST.indexOf(tvRedFrequency.text.toString()).toString() else "",
                if (etIndicatorRedValue.text.toString().isNotEmpty()) etIndicatorRedValue.text.toString().toInt() else -1,
                tvIndicatorRedName.text.toString())
        if (!triggerRed.validateModel()) {
            Toasty.error(this, "Red trigger is not valid, please double check!").show()
            return
        }
        if (!mIsCountryContext && mIndicatorModel.hazardScenario.validateModel().isNotEmpty()) {
            Toasty.error(this, mIndicatorModel.hazardScenario.validateModel()).show()
            return
        }
        mIndicatorModel.trigger = listOf<ModelTrigger>(triggerGreen, triggerAmber, triggerRed)
        mIndicatorModel.name = tvAddIndicatorName.text.toString()
        when (mIndicatorModel.triggerSelected) {
            0 -> {
                mIndicatorModel.dueDate = getDueDate(mIndicatorModel.trigger[0])
            }
            1 -> {
                mIndicatorModel.dueDate = getDueDate(mIndicatorModel.trigger[1])
            }
            else -> {
                mIndicatorModel.dueDate = getDueDate(mIndicatorModel.trigger[2])
            }
        }
        mIndicatorModel.updatedAt = DateTime.now().millis

        if (mIndicatorModel.validateModel().isNotEmpty()) {
            Toasty.error(this, mIndicatorModel.validateModel()).show()
            return
        }

        when {
            mIndicatorModel.geoLocation == SUBNATIONAL -> {
                if (mIndicatorModel.validateLocation().isNotEmpty()) {
                    Timber.d(mIndicatorModel.validateLocation())
                    Toasty.error(this, mIndicatorModel.validateLocation()).show()
                    return
                }
                mIndicatorModel.gps = null
            }
            mIndicatorModel.geoLocation == NATIONAL -> {
                mIndicatorModel.affectedLocation = null
                mIndicatorModel.gps = null
            }
            else -> {
                if (mIndicatorModel.validateGps().isNotEmpty()) {
                    Toasty.error(this, mIndicatorModel.validateGps()).show()
                    return
                }
                mIndicatorModel.affectedLocation = null
            }
        }


        Timber.d(mIndicatorModel.toString())
        pbAddIndicator.visibility = View.VISIBLE

        pushToDatabase(mIndicatorModel)
    }

    private fun pushToDatabase(mIndicatorModel: ModelIndicator) {
        mViewModel.addIndicator(mIndicatorModel)
                ?.addOnCompleteListener {
                    pbAddIndicator.visibility = View.GONE
                    Toasty.success(this, "Indicator added successfully").show()
                    finish()
                }
                ?.addOnFailureListener {
                    Toasty.error(this, "Failed to add indicator, please retry").show()
                }
    }

    private fun getDueDate(modelTrigger: ModelTrigger): Long =
            when (TRIGGER_FREQUENCY_LIST.indexOf(modelTrigger.durationType)) {
                0 -> {
                    DateTime.now().plusHours(modelTrigger.frequencyValue).millis
                }
                1 -> {
                    DateTime.now().plusDays(modelTrigger.frequencyValue).millis
                }
                2 -> {
                    DateTime.now().plusWeeks(modelTrigger.frequencyValue).millis
                }
                else -> {
                    DateTime.now().plusMonths(modelTrigger.frequencyValue).millis
                }
            }

    override fun sourceRemovePosition(position: Int) {
        mSources.removeAt(position)
        mSourceAdapter.notifyItemRemoved(position)
    }

    override fun areaRemovePosition(position: Int) {
        Timber.d("delete area position: %s", position)
        mAreas.removeAt(position)
        mAreaAdapter.notifyItemRemoved(position)
    }

}

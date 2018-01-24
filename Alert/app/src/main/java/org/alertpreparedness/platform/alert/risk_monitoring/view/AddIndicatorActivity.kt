package org.alertpreparedness.platform.alert.risk_monitoring.view

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.*
import com.google.firebase.database.DatabaseReference
import com.tbruyelle.rxpermissions2.RxPermissions
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_add_indicator.*
import kotlinx.android.synthetic.main.content_add_indicator.*
import org.alertpreparedness.platform.alert.AlertApplication
import org.alertpreparedness.platform.alert.BaseActivity
import org.alertpreparedness.platform.alert.R
import org.alertpreparedness.platform.alert.risk_monitoring.adapter.AreaRVAdapter
import org.alertpreparedness.platform.alert.risk_monitoring.adapter.OnAreaDeleteListener
import org.alertpreparedness.platform.alert.risk_monitoring.adapter.OnSourceDeleteListener
import org.alertpreparedness.platform.alert.risk_monitoring.adapter.SourceRVAdapter
import org.alertpreparedness.platform.alert.risk_monitoring.dialog.*
import org.alertpreparedness.platform.alert.risk_monitoring.model.*
import org.alertpreparedness.platform.alert.risk_monitoring.view_model.ActiveRiskViewModel
import org.alertpreparedness.platform.alert.risk_monitoring.view_model.AddIndicatorViewModel
import org.alertpreparedness.platform.alert.utils.AppUtils
import org.alertpreparedness.platform.alert.utils.Constants
import org.alertpreparedness.platform.alert.utils.FirebaseHelper
import org.alertpreparedness.platform.alert.utils.PreferHelper
import org.joda.time.DateTime
import timber.log.Timber
import java.lang.IllegalArgumentException
import java.util.*

class AddIndicatorActivity : BaseActivity(), OnSourceDeleteListener, OnAreaDeleteListener {

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

    private var mIndicatorModel = ModelIndicator()
    private lateinit var mViewModel: AddIndicatorViewModel
    private var mHazards: List<ModelHazard>? = null
    private var mIsCountryContext = false
    private var mStaff: ArrayList<ModelUserPublic>? = null

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationCallback: LocationCallback

    private val mHazardOtherNamesMap = mutableMapOf<String, String>()
    private var mCountryJsonList: List<CountryJsonData> = listOf()

    //edit
    private var mHazardId: String? = null
    private var mIndicatorId: String? = null
    private var mNetworkId: String? = null
    private var mNetworkCountryId: String? = null
    private lateinit var mRiskViewModel: ActiveRiskViewModel
    private var mEditStatus = EDIT_NO_HAZARD_CHANGE
    private lateinit var mLoadedHazardForIndicator: ModelHazard

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, AddIndicatorActivity::class.java)
            context.startActivity(intent)
        }

        fun startActivityWithValues(context: Context, hazardId: String, indicatorId: String, networkId: String?, networkCountryId: String?) {
            val intent = Intent(context, AddIndicatorActivity::class.java)
            intent.putExtra(HAZARD_ID, hazardId)
            intent.putExtra(INDICATOR_ID, indicatorId)
            networkId?.apply { intent.putExtra(NETWORK_ID, networkId) }
            networkCountryId?.apply { intent.putExtra(NETWORK_COUNTRY_ID, networkCountryId) }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        val SELECTED_LOCATION = "selected_location"
        val STAFF_SELECTION = "staff_selection"
        val ASSIGN_POSITION = "assign_position"
        val LOCATION_LIST = listOf("National", "Subnational", "Use my location")
        val NATIONAL = 0
        val SUBNATIONAL = 1
        val TRIGGER_FREQUENCY_LIST = listOf("Hours", "Days", "Weeks", "Months")
        val AREA_REQUEST_CODE = 100
        val HAZARD_ID = "hazard_id"
        val INDICATOR_ID = "indicator_id"
        val NETWORK_ID = "network_id"
        val NETWORK_COUNTRY_ID = "network_country_id"

        val EDIT_NO_HAZARD_CHANGE = 0
        val EDIT_FROM_COUNTRY_WITH_HAZARD_CHANGE = 1
        val EDIT_FROM_NETWORK_WITH_HAZARD_CHANGE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_indicator)
        mViewModel = ViewModelProviders.of(this).get(AddIndicatorViewModel::class.java)
        mRiskViewModel = ViewModelProviders.of(this).get(ActiveRiskViewModel::class.java)
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
        mHazardId = intent.getStringExtra(HAZARD_ID)
        mIndicatorId = intent.getStringExtra(INDICATOR_ID)
        mNetworkId = intent.getStringExtra(NETWORK_ID)
        mNetworkCountryId = intent.getStringExtra(NETWORK_COUNTRY_ID)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSources = mutableListOf()
        mAreas = mutableListOf()
//        mStaff.add()
        mViewModel.getStaffLive().observe(this, Observer { users ->
            println("users = ${users}")
            mStaff = ArrayList(users)
        })
        mViewModel.getCountryJsonDataLive().observe(this, Observer { countryList ->
            countryList?.let {
                mCountryJsonList = countryList
                if (countryList.size == 248) {
                    mAreaAdapter = AreaRVAdapter(mAreas, countryList)
                    rvLocationSubNational.adapter = mAreaAdapter
                    mAreaAdapter.setOnAreaDeleteListener(this)
                    if (pbAddIndicator.isShown) {
                        pbAddIndicator.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        val supportActionBar = supportActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (mHazardId != null && mIndicatorId != null) getString(R.string.edit_indicator) else getString(R.string.add_indicator)
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
                            pair?.first?.let {
                                mHazardOtherNamesMap[pair.first] = pair.second
                                Timber.d("other map: %s", mHazardOtherNamesMap.toString())
                            }
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

        //edit load data back
        if (mHazardId != null && mIndicatorId != null) {
            mRiskViewModel.getLiveIndicatorModel(mHazardId as String, mIndicatorId as String).observe(this, Observer { model ->
                model?.apply {
                    mIndicatorModel = model
                    mLoadedHazardForIndicator = model.hazardScenario
                    if (model.geoLocation == SUBNATIONAL) {
                        pbAddIndicator.visibility = View.VISIBLE
                    }
                    loadDataBack(model)
                    Timber.d(mIndicatorModel.toString())
                }
            })
        }
    }

    private fun loadDataBack(model: ModelIndicator) {
        when {
            model.hazardScenario.key == "countryContext" && model.hazardScenario.hazardScenario == -2 -> {
                tvSelectHazard.text = getString(R.string.country_context)
                mIsCountryContext = true
            }
            model.hazardScenario.hazardScenario != -1 -> {
                tvSelectHazard.text = Constants.HAZARD_SCENARIO_NAME[model.hazardScenario.hazardScenario]
                mIsCountryContext = false
            }
            else -> {
                Timber.d("other name id: %s", model.hazardScenario.otherName)
                model.hazardScenario.otherName?.apply {
                    mRiskViewModel.getLiveOtherHazardName(model.hazardScenario.otherName as String).observe(this@AddIndicatorActivity, Observer { name ->
                        tvSelectHazard.text = name
                        mIsCountryContext = false
                    })
                }
            }
        }
        tvAddIndicatorName.setText(model.name)
        mSources.addAll(model.source)
        val green = model.trigger[Constants.TRIGGER_GREEN]
        tvIndicatorGreenName.setText(green.triggerValue)
        etIndicatorGreenValue.setText(green.frequencyValue)
        tvGreenFrequency.text = TRIGGER_FREQUENCY_LIST[green.durationType.toInt()]
        val amber = model.trigger[Constants.TRIGGER_AMBER]
        tvIndicatorAmberName.setText(amber.triggerValue)
        etIndicatorAmberValue.setText(amber.frequencyValue)
        tvAmberFrequency.text = TRIGGER_FREQUENCY_LIST[amber.durationType.toInt()]
        val red = model.trigger[Constants.TRIGGER_RED]
        tvIndicatorRedName.setText(red.triggerValue)
        etIndicatorRedValue.setText(red.frequencyValue)
        tvRedFrequency.text = TRIGGER_FREQUENCY_LIST[red.durationType.toInt()]
        mViewModel.getStaffLive().observe(this, Observer { staffs ->
            model.assignee?.apply {
                staffs?.find {
                    it.id == model.assignee
                }?.let {
                    tvAssignTo.text = String.format("%s %s", it.firstName, it.lastName)
                    //need to +1 cause position 0 will be unassigned
                    mSelectedAssignPosition = staffs.indexOfFirst { staff -> staff.id == model.assignee } + 1
                }
            }
        })
        tvIndicatorLocation.text = LOCATION_LIST[model.geoLocation]
        mSelectedLocation = model.geoLocation
        when (model.geoLocation) {
            NATIONAL -> {
                rvLocationSubNational.visibility = View.GONE
                tvIndicatorSelectSubNational.visibility = View.GONE
                tvIndicatorMyLocation.visibility = View.GONE
            }
            SUBNATIONAL -> {
                rvLocationSubNational.visibility = View.VISIBLE
                tvIndicatorSelectSubNational.visibility = View.VISIBLE
                tvIndicatorMyLocation.visibility = View.GONE
                model.affectedLocation?.apply { mAreas = this.toMutableList() }
            }
            else -> {
                rvLocationSubNational.visibility = View.GONE
                tvIndicatorSelectSubNational.visibility = View.GONE
                tvIndicatorMyLocation.visibility = View.VISIBLE
                tvIndicatorMyLocation.text = String.format("%s\n(%s, %s)", model.gps?.address, model.gps?.latitude, model.gps?.longitude)
            }
        }
    }

    private fun initListeners() {
        tvSelectHazard.setOnClickListener {
            AppUtils.hideSoftKeyboard(AlertApplication.getContext(), tvSelectHazard)
            Timber.d("show popup menu")
            mPopupMenu.show()
        }

        mPopupMenu.setOnMenuItemClickListener { p0 ->
            Timber.d("menu: %s", p0?.title)
            tvSelectHazard.text = p0?.title
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

            //check edit status
            if (mHazardId != null && mIndicatorId != null) {
                val loadedHazardName = when {
                    mLoadedHazardForIndicator.key == "countryContext" -> {
                        getString(R.string.country_context)
                    }
                    mLoadedHazardForIndicator.hazardScenario == -1 -> {
                        mHazardOtherNamesMap[mLoadedHazardForIndicator.otherName]
                    }
                    else -> {
                        Constants.HAZARD_SCENARIO_NAME[mLoadedHazardForIndicator.hazardScenario]
                    }
                }
                mEditStatus = when {
                    loadedHazardName == tvSelectHazard.text -> {
                        EDIT_NO_HAZARD_CHANGE
                    }
                    loadedHazardName != tvSelectHazard.text && mNetworkId == null -> {
                        EDIT_FROM_COUNTRY_WITH_HAZARD_CHANGE
                    }
                    loadedHazardName != tvSelectHazard.text && mNetworkId != null -> {
                        EDIT_FROM_NETWORK_WITH_HAZARD_CHANGE
                    }
                    else -> {
                        throw IllegalArgumentException("Loaded hazard name not matching anything!")
                    }
                }
            }
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
            AppUtils.hideSoftKeyboard(AlertApplication.getContext(), tvGreenFrequency)
            mPopupMenuFrequencyGreen.show()
        }

        mPopupMenuFrequencyGreen.setOnMenuItemClickListener { menuItem ->
            tvGreenFrequency.text = menuItem.title
            true
        }

        tvAmberFrequency.setOnClickListener {
            AppUtils.hideSoftKeyboard(AlertApplication.getContext(), tvAmberFrequency)
            mPopupMenuFrequencyAmber.show()
        }

        mPopupMenuFrequencyAmber.setOnMenuItemClickListener { menuItem ->
            tvAmberFrequency.text = menuItem.title
            true
        }

        tvRedFrequency.setOnClickListener {
            AppUtils.hideSoftKeyboard(AlertApplication.getContext(), tvRedFrequency)
            mPopupMenuFrequencyRed.show()
        }

        mPopupMenuFrequencyRed.setOnMenuItemClickListener { menuItem ->
            tvRedFrequency.text = menuItem.title
            true
        }

        tvAssignTo.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(ASSIGN_POSITION, mSelectedAssignPosition)
            mStaff?.let {
                println("mStaff = ${mStaff}")
                bundle.putSerializable(STAFF_SELECTION, mStaff) }
            println("bundle = ${bundle}")
            mDialogAssign.arguments = bundle
            mDialogAssign.show(supportFragmentManager, "dialog_assign")
        }

        mDialogAssign.setOnAssignToListener(object : AssignToListener {
            override fun userAssignedTo(user: ModelUserPublic?, position: Int) {
                user?.let { tvAssignTo.text = String.format("%s %s", user.firstName, user.lastName) }
                mSelectedAssignPosition = position
                if (user?.id?.isNotEmpty()!!) {
                    mIndicatorModel.assignee = user.id
                } else {
                    mIndicatorModel.assignee = null
                }
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
                fetchAddress(location)
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
                        pbAddIndicator.visibility = View.VISIBLE
                        mFusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            if (location != null) {
                                Timber.d("location: %s / %s", location.longitude, location.latitude)
                                tvIndicatorMyLocation.text = String.format("(%s,%s)", location.latitude, location.longitude)
                                mIndicatorModel.gps = ModelGps(latitude = location.latitude.toString(), longitude = location.longitude.toString())
                                fetchAddress(location)
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
                                    pbAddIndicator.visibility = View.GONE
                                    Timber.d("location settings failed!!!")
                                })
                            }
                        }
                    } else {
                        Timber.d("Permission request denied")
                    }
                })

    }

    private fun fetchAddress(location: Location?) {
        if (location != null) {
            mViewModel.getAddressLive(location).observe(this, Observer { address ->
                pbAddIndicator.visibility = View.GONE
                if (address!!.isNotEmpty()) {
                    tvIndicatorMyLocation.text = String.format("%s, %s", address, tvIndicatorMyLocation.text)
                    mIndicatorModel.gps?.address = address
                }
            })
        } else {
            pbAddIndicator.visibility = View.GONE
            Toasty.warning(this, "Location is not available").show()
        }
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
                Timber.d("nothing clicked")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveIndicator() {
        mIndicatorModel.source = mSources
        val triggerGreen = ModelTrigger(if (TRIGGER_FREQUENCY_LIST.contains(tvGreenFrequency.text.toString())) TRIGGER_FREQUENCY_LIST.indexOf(tvGreenFrequency.text.toString()).toString() else "",
                if (etIndicatorGreenValue.text.toString().isNotEmpty()) etIndicatorGreenValue.text.toString() else "",
                tvIndicatorGreenName.text.toString())
        if (!triggerGreen.validateModel()) {
            Toasty.error(this, "Green trigger is not valid, please double check!").show()
            return
        }
        val triggerAmber = ModelTrigger(if (TRIGGER_FREQUENCY_LIST.contains(tvAmberFrequency.text.toString())) TRIGGER_FREQUENCY_LIST.indexOf(tvAmberFrequency.text.toString()).toString() else "",
                if (etIndicatorAmberValue.text.toString().isNotEmpty()) etIndicatorAmberValue.text.toString() else "",
                tvIndicatorAmberName.text.toString())
        if (!triggerAmber.validateModel()) {
            Toasty.error(this, "Amber trigger is not valid, please double check!").show()
            return
        }
        val triggerRed = ModelTrigger(if (TRIGGER_FREQUENCY_LIST.contains(tvRedFrequency.text.toString())) TRIGGER_FREQUENCY_LIST.indexOf(tvRedFrequency.text.toString()).toString() else "",
                if (etIndicatorRedValue.text.toString().isNotEmpty()) etIndicatorRedValue.text.toString() else "",
                tvIndicatorRedName.text.toString())
        if (!triggerRed.validateModel()) {
            Toasty.error(this, "Red trigger is not valid, please double check!").show()
            return
        }
        if (!mIsCountryContext && mIndicatorModel.hazardScenario.validateModel().isNotEmpty()) {
            Toasty.error(this, mIndicatorModel.hazardScenario.validateModel()).show()
            return
        }
        mIndicatorModel.trigger = listOf(triggerGreen, triggerAmber, triggerRed)
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
                mIndicatorModel.resetLevels()
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

        //actual add or edit indicator
        when (mIndicatorModel.id) {
            null -> {
                pushToDatabase(mIndicatorModel)
            }
            else -> {
                if (mHazardId != null && mIndicatorId != null) {
                    Timber.d(mIndicatorModel.toString())
                    //three more conditions to do update based on edit status
                    when (mEditStatus) {
                        EDIT_NO_HAZARD_CHANGE -> {
                            Timber.d("EDIT_NO_HAZARD_CHANGE")
//                            mRiskViewModel.updateIndicatorModel(mHazardId as String, mIndicatorId as String, mIndicatorModel.copy(id = null))
                            val indicatorRef = FirebaseHelper.getIndicatorRef(PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS), mHazardId, mIndicatorId)
                            when (mIsCountryContext) {
                                true -> {
                                    val context = ModelHazardCountryContext()
                                    indicatorRef.setValue(mIndicatorModel.copy(id = null)).continueWith {
                                        indicatorRef.child("hazardScenario").setValue(context)
                                    }
                                }
                                else -> {
                                    val fixData = mutableMapOf<String, Any>("isActive" to mIndicatorModel.hazardScenario.isActive, "isSeasonal" to mIndicatorModel.hazardScenario.isSeasonal)
                                    indicatorRef.setValue(mIndicatorModel.copy(id = null)).continueWith {
                                        indicatorRef.child("hazardScenario").updateChildren(fixData)
                                        indicatorRef.child("hazardScenario").child("active").removeValue()
                                        indicatorRef.child("hazardScenario").child("seasonal").removeValue()
                                    }
                                }
                            }
                        }
                        EDIT_FROM_COUNTRY_WITH_HAZARD_CHANGE -> {
                            Timber.d("EDIT_FROM_COUNTRY_WITH_HAZARD_CHANGE")
                            when (mIsCountryContext) {
                                true -> {
                                    val refCountryContext = FirebaseHelper.getIndicatorRef(PreferHelper.getString(this, Constants.APP_STATUS), PreferHelper.getString(this, Constants.COUNTRY_ID), mIndicatorId)
                                    updateAndClearOldForContext(refCountryContext, mIndicatorModel)
                                }
                                else -> {
                                    when (mIndicatorModel.hazardScenario.hazardScenario) {
                                        -1 -> {
                                            Timber.d("edit to custom hazard")
                                            val find = mHazards?.filter { it.hazardScenario == -1 }?.find { it.otherName == mIndicatorModel.hazardScenario.otherName }
                                            Timber.d(find.toString())
                                            find?.id?.apply {
                                                val refNewHazard = FirebaseHelper.getIndicatorRef(PreferHelper.getString(this@AddIndicatorActivity, Constants.APP_STATUS), find.id, mIndicatorId)
                                                updateAndClearOld(refNewHazard, mIndicatorModel)
                                            }
                                        }
                                        else -> {
                                            Timber.d("edit to standard hazard")
                                            val find = mHazards?.filter { it.hazardScenario != -1 }?.find { it.hazardScenario == mIndicatorModel.hazardScenario.hazardScenario }
                                            Timber.d("found hazard id: %s", find?.id)
                                            find?.id?.apply {
                                                val refNewHazard = FirebaseHelper.getIndicatorRef(PreferHelper.getString(this@AddIndicatorActivity, Constants.APP_STATUS), find.id, mIndicatorId)
                                                updateAndClearOld(refNewHazard, mIndicatorModel)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        EDIT_FROM_NETWORK_WITH_HAZARD_CHANGE -> {
                            Timber.d("EDIT_FROM_NETWORK_WITH_HAZARD_CHANGE")
                            Timber.d("check this, %s", mIndicatorModel.hazardScenario)
                            when (mIsCountryContext) {
                                true -> {
                                    val indicatorRef = FirebaseHelper.getIndicatorRef(PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS), PreferHelper.getString(this, Constants.COUNTRY_ID), mIndicatorId)
                                    updateAndClearOldForContextNetwork(indicatorRef, mIndicatorModel)
                                }
                                else -> {
                                    when {
                                        mIndicatorModel.hazardScenario.hazardScenario == -1 -> {
                                            Timber.d("edit to custom hazard")
                                            val find = mHazards?.filter { it.hazardScenario == -1 }?.find { it.otherName == mIndicatorModel.hazardScenario.otherName }
                                            Timber.d(find.toString())
                                            find?.id?.apply {
                                                val refNewHazard = FirebaseHelper.getIndicatorRef(PreferHelper.getString(this@AddIndicatorActivity, Constants.APP_STATUS), find.id, mIndicatorId)
                                                updateAndClearOldNetwork(refNewHazard, mIndicatorModel)
                                            }
                                        }
                                        else -> {
                                            Timber.d("edit to standard hazard")
                                            val find = mHazards?.filter { it.hazardScenario != -1 }?.find { it.hazardScenario == mIndicatorModel.hazardScenario.hazardScenario }
                                            Timber.d("found hazard id: %s", find?.id)
                                            find?.id?.apply {
                                                val refNewHazard = FirebaseHelper.getIndicatorRef(PreferHelper.getString(this@AddIndicatorActivity, Constants.APP_STATUS), find.id, mIndicatorId)
                                                updateAndClearOldNetwork(refNewHazard, mIndicatorModel)
                                            }
                                        }
                                    }
                                }
                            }

                        }
                        else -> {
                            throw IllegalArgumentException("No matching edit condition!")
                        }
                    }
                    finish()
                }

            }
        }

    }

    private fun updateAndClearOld(refCountryContext: DatabaseReference?, mIndicatorModel: ModelIndicator) {
        refCountryContext?.setValue(mIndicatorModel.copy(id = null))?.continueWith {
            refCountryContext.child("hazardScenario").child("isActive").setValue(mIndicatorModel.hazardScenario.isActive)
            refCountryContext.child("hazardScenario").child("isSeasonal").setValue(mIndicatorModel.hazardScenario.isSeasonal)
            refCountryContext.child("hazardScenario").child("key").setValue(mIndicatorModel.hazardScenario.id)
            refCountryContext.child("hazardScenario").child("active").removeValue()
            refCountryContext.child("hazardScenario").child("seasonal").removeValue()
            refCountryContext.child("hazardScenario").child("id").removeValue()
            val refToDelete = FirebaseHelper.getIndicatorRef(PreferHelper.getString(this, Constants.APP_STATUS), mHazardId, mIndicatorId)
            refToDelete.removeValue()
        }
    }

    private fun updateAndClearOldNetwork(refCountryContext: DatabaseReference?, mIndicatorModel: ModelIndicator) {
        refCountryContext?.setValue(mIndicatorModel.copy(id = null))?.continueWith {
            refCountryContext.child("hazardScenario").child("isActive").setValue(mIndicatorModel.hazardScenario.isActive)
            refCountryContext.child("hazardScenario").child("isSeasonal").setValue(mIndicatorModel.hazardScenario.isSeasonal)
            refCountryContext.child("hazardScenario").child("key").setValue(mIndicatorModel.hazardScenario.id)
            refCountryContext.child("hazardScenario").child("active").removeValue()
            refCountryContext.child("hazardScenario").child("seasonal").removeValue()
            refCountryContext.child("hazardScenario").child("id").removeValue()
        }
    }

    private fun updateAndClearOldForContext(refCountryContext: DatabaseReference?, mIndicatorModel: ModelIndicator) {
        refCountryContext?.setValue(mIndicatorModel.copy(id = null))?.continueWith {
            val hazardContext = ModelHazardCountryContext()
            refCountryContext.child("hazardScenario").setValue(hazardContext)
            val refToDelete = FirebaseHelper.getIndicatorRef(PreferHelper.getString(this, Constants.APP_STATUS), mHazardId, mIndicatorId)
            refToDelete.removeValue()
        }
    }

    private fun updateAndClearOldForContextNetwork(refCountryContext: DatabaseReference?, mIndicatorModel: ModelIndicator) {
        refCountryContext?.setValue(mIndicatorModel.copy(id = null))?.continueWith {
            val hazardContext = ModelHazardCountryContext()
            refCountryContext.child("hazardScenario").setValue(hazardContext)
        }
    }

    private fun pushToDatabase(mIndicatorModel: ModelIndicator) {
        mViewModel.addIndicator(mIndicatorModel, mIsCountryContext)
        Toasty.success(this, "Indicator added successfully").show()
        finish()
    }

    private fun getDueDate(modelTrigger: ModelTrigger): Long =
            when (TRIGGER_FREQUENCY_LIST.indexOf(modelTrigger.durationType)) {
                0 -> {
                    DateTime.now().plusHours(modelTrigger.frequencyValue.toInt()).millis
                }
                1 -> {
                    DateTime.now().plusDays(modelTrigger.frequencyValue.toInt()).millis
                }
                2 -> {
                    DateTime.now().plusWeeks(modelTrigger.frequencyValue.toInt()).millis
                }
                else -> {
                    DateTime.now().plusMonths(modelTrigger.frequencyValue.toInt()).millis
                }
            }

    override fun sourceRemovePosition(position: Int) {
        mSources.removeAt(position)
        mSourceAdapter.notifyItemRemoved(position)
    }

    override fun areaRemovePosition(position: Int) {
        mAreas.removeAt(position)
        mAreaAdapter.notifyItemRemoved(position)
    }

}

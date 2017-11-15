package org.alertpreparedness.platform.alert.risk_monitoring

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_select_area.*
import kotlinx.android.synthetic.main.content_select_area.*
import org.alertpreparedness.platform.alert.R
import org.json.JSONObject
import timber.log.Timber

class SelectAreaActivity : AppCompatActivity() {

    companion object {
        val SELECT_DIALOG_ARGS = "select_country_args"
        val SELECT_LEVEL1_DIALOG_ARGS = "select_level1_args"
        val SELECT_LEVEL2_DIALOG_ARGS = "select_level2_args"
    }

    private lateinit var mCountryDataList: ArrayList<CountryJsonData>
    private lateinit var mSelectCountryDialog: SelectCountryDialog
    private lateinit var mSelectLevel1Dialog: SelectLevel1Dialog
    private lateinit var mSelectLevel2Dialog: SelectLevel2Dialog

    private var mCountrySelected = -1
    private var mLevel1Selected: Int? = null
    private var mLevel2Selected: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_area)
        initData()
        initViews()
        initListeners()
    }

    private fun initData() {
        mCountryDataList = arrayListOf()
        mSelectCountryDialog = SelectCountryDialog()
        mSelectLevel1Dialog = SelectLevel1Dialog()
        mSelectLevel2Dialog = SelectLevel2Dialog()
        RiskMonitoringService.readJsonFile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ fileText ->
                    val jsonObject = JSONObject(fileText)
                    RiskMonitoringService.mapJasonToCountryData(jsonObject, Gson())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ countryData: CountryJsonData ->
                                Timber.d("Country id is: %s, level 1: %s", countryData.countryId, countryData.levelOneValues?.size)
                                mCountryDataList.add(countryData)
                            })
                })

//        Thread(Runnable {
//            (0..248)
//                    .map { it.toString() }
//                    .map {
//
//                        if (!jsonObject.isNull(it)) {
////                            Timber.d(it)
//                            val value = jsonObject.get(it).toString()
//                            val countryData = gson.fromJson(value, CountryJsonData::class.java)
////                            Timber.d(countryData.toString())
//                            mCountryDataMap.put(it, countryData)
//                        }
//                    }
//        }).start()
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Area"
    }

    private fun initListeners() {
        llAreaCountry.setOnClickListener {
            Timber.d("country data size: %s", mCountryDataList.size)
            val bundle = Bundle()
            bundle.putSerializable(SELECT_DIALOG_ARGS, mCountryDataList)
            mSelectCountryDialog.arguments = bundle
            mSelectCountryDialog.show(supportFragmentManager, SELECT_DIALOG_ARGS)

        }

        mSelectCountryDialog.setOnCountrySelectedListener(object : OnCountrySelectedListener {
            override fun selectedCountry(countryJsonData: CountryJsonData) {
                if (countryJsonData.countryId != mCountrySelected) {
                    mLevel1Selected = -1
                    mLevel2Selected = -1
                    tvSelectLevel1.text = ""
                    tvSelectLevel1.hint = "Optional"
                    tvSelectLevel2.text = ""
                    tvSelectLevel2.hint = "Optional"
                }
                mCountrySelected = countryJsonData.countryId
                tvSelectCountry.text = countryJsonData.countryId.toString()
            }
        })

        llAreaLevel1.setOnClickListener {
            if (mCountrySelected == -1) {
                Toasty.warning(this, "Please select country first!").show()
                return@setOnClickListener
            }
            val bundle = Bundle()
            bundle.putSerializable(SELECT_DIALOG_ARGS, mCountryDataList)
            bundle.putInt(SELECT_LEVEL1_DIALOG_ARGS, mCountrySelected)
            mSelectLevel1Dialog.arguments = bundle
            mSelectLevel1Dialog.show(supportFragmentManager, SELECT_LEVEL1_DIALOG_ARGS)
        }

        mSelectLevel1Dialog.setOnLevel1SelectedListener(object : OnLevel1SelectedListener {
            override fun selectedLevel1(level1Value: LevelOneValuesItem?) {
                if (level1Value?.id != mLevel1Selected) {
                    mLevel2Selected = -1
                    tvSelectLevel2.text = ""
                    tvSelectLevel2.hint = "Optional"
                }
                mLevel1Selected = level1Value?.id
                tvSelectLevel1.text = level1Value?.value
            }

        })

        llAreaLevel2.setOnClickListener {
            if (mCountrySelected == -1) {
                Toasty.warning(this, "Please select country first!").show()
                return@setOnClickListener
            } else if (mLevel1Selected == null) {
                Toasty.warning(this, "Please select level 1 value first!").show()
                return@setOnClickListener
            }

            val bundle = Bundle()
            bundle.putSerializable(SELECT_DIALOG_ARGS, mCountryDataList)
            bundle.putInt(SELECT_LEVEL1_DIALOG_ARGS, mCountrySelected)
            bundle.putInt(SELECT_LEVEL2_DIALOG_ARGS, mLevel1Selected as Int)
            mSelectLevel2Dialog.arguments = bundle
            mSelectLevel2Dialog.show(supportFragmentManager, SELECT_LEVEL2_DIALOG_ARGS)
        }

        mSelectLevel2Dialog.setOnLevel2SelectedListener(object : OnLevel2SelectedListener{
            override fun selectedLevel2(level2Value: LevelTwoValuesItem?) {
                mLevel2Selected = level2Value?.id
                tvSelectLevel2.text = level2Value?.value
            }
        })

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
//                val i = Intent()
//                setResult()
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

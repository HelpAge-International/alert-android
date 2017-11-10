package org.alertpreparedness.platform.alert.risk_monitoring

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_select_area.*
import kotlinx.android.synthetic.main.content_select_area.*
import org.alertpreparedness.platform.alert.R
import org.json.JSONObject
import timber.log.Timber

class SelectAreaActivity : AppCompatActivity() {

    private var mCountryDataMap: MutableMap<String, CountryJsonData> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_area)
        initData()
        initViews()
        initListeners()
    }

    private fun initData() {
        val gson = Gson()
        val fileText: String = assets.open("country_levels_values.json").bufferedReader().use {
            it.readText()
        }
        val jsonObject = JSONObject(fileText)
//        Observable.range(0,248)
//                .map {
//                    if (!jsonObject.isNull(it.toString())) {
////                            Timber.d(it)
//                        val value = jsonObject.get(it.toString()).toString()
//                        val countryData = gson.fromJson(value, CountryJsonData::class.java)
//                        countryData.countryId = it
////                            Timber.d(countryData.toString())
////                        mCountryDataMap.put(it, countryData)
//                    }
//                }
//                .subscribe()
        Thread(Runnable {
            (0..248)
                    .map { it.toString() }
                    .map {

                        if (!jsonObject.isNull(it)) {
//                            Timber.d(it)
                            val value = jsonObject.get(it).toString()
                            val countryData = gson.fromJson(value, CountryJsonData::class.java)
//                            Timber.d(countryData.toString())
                            mCountryDataMap.put(it, countryData)
                        }
                    }
        }).start()
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Area"
    }

    private fun initListeners() {
        llAreaCountry.setOnClickListener {
            Timber.d("map size: %s", mCountryDataMap.size)
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
//                val i = Intent()
//                setResult()
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

package org.alertpreparedness.platform.v1.risk_monitoring.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import es.dmoral.toasty.Toasty
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_select_area.pbSelectAreaLoading
import kotlinx.android.synthetic.main.activity_select_area.toolbar
import kotlinx.android.synthetic.main.content_select_area.llAreaCountry
import kotlinx.android.synthetic.main.content_select_area.llAreaLevel1
import kotlinx.android.synthetic.main.content_select_area.llAreaLevel2
import kotlinx.android.synthetic.main.content_select_area.tvSelectCountry
import kotlinx.android.synthetic.main.content_select_area.tvSelectLevel1
import kotlinx.android.synthetic.main.content_select_area.tvSelectLevel2
import org.alertpreparedness.platform.v1.BaseActivity
import org.alertpreparedness.platform.R
import org.alertpreparedness.platform.v1.risk_monitoring.dialog.OnCountrySelectedListener
import org.alertpreparedness.platform.v1.risk_monitoring.dialog.OnLevel1SelectedListener
import org.alertpreparedness.platform.v1.risk_monitoring.dialog.OnLevel2SelectedListener
import org.alertpreparedness.platform.v1.risk_monitoring.dialog.SelectCountryDialog
import org.alertpreparedness.platform.v1.risk_monitoring.dialog.SelectLevel1Dialog
import org.alertpreparedness.platform.v1.risk_monitoring.dialog.SelectLevel2Dialog
import org.alertpreparedness.platform.v1.risk_monitoring.model.CountryJsonData
import org.alertpreparedness.platform.v1.risk_monitoring.model.LevelOneValuesItem
import org.alertpreparedness.platform.v1.risk_monitoring.model.LevelTwoValuesItem
import org.alertpreparedness.platform.v1.risk_monitoring.model.ModelIndicatorLocation
import org.alertpreparedness.platform.v1.risk_monitoring.view_model.SelectAreaViewModel
import org.alertpreparedness.platform.v1.utils.Constants
import timber.log.Timber

@Deprecated("Needs rewriting for V2")
class SelectAreaActivity : BaseActivity() {

    private val mDisposables = CompositeDisposable()

    companion object {
        val SELECT_DIALOG_ARGS = "select_country_args"
        val SELECT_LEVEL1_DIALOG_ARGS = "select_level1_args"
        val SELECT_LEVEL2_DIALOG_ARGS = "select_level2_args"
        val SELECTED_AREA = "selected_area"
        val SELECTED_AREA_TEXT: String = "selected_area_text"
    }

    private lateinit var mCountryDataList: ArrayList<CountryJsonData>
    private lateinit var mSelectCountryDialog: SelectCountryDialog
    private lateinit var mSelectLevel1Dialog: SelectLevel1Dialog
    private lateinit var mSelectLevel2Dialog: SelectLevel2Dialog
    private lateinit var mViewModel: SelectAreaViewModel

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

    override fun onDestroy() {
        super.onDestroy()
        mDisposables.clear()
    }

    private fun initData() {
        mViewModel = ViewModelProviders.of(this).get(SelectAreaViewModel::class.java)
        mCountryDataList = arrayListOf()
        mSelectCountryDialog = SelectCountryDialog()
        mSelectLevel1Dialog = SelectLevel1Dialog()
        mSelectLevel2Dialog = SelectLevel2Dialog()
        mViewModel.getCountryJsonDataLive().observe(this, Observer { countryDataList ->
            mCountryDataList = ArrayList(countryDataList)
            if (mCountryDataList.size > 240 && pbSelectAreaLoading.isShown) {
                pbSelectAreaLoading.visibility = View.GONE
            }
        })
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Area"

        mViewModel.getSelectedCountryLive().observe(this, Observer { country ->
            country?.let {
                if(it.location != -1) {
                    tvSelectCountry.text = Constants.COUNTRIES[it.location]
                    mCountrySelected = it.location
                }
            }
        })
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
                tvSelectCountry.text = Constants.COUNTRIES[countryJsonData.countryId]
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
            } else if (mLevel1Selected == null || mLevel1Selected == -1) {
                Toasty.warning(this, "Please select level 1 value first!").show()
                return@setOnClickListener
            } else {
                val bundle = Bundle()
                bundle.putSerializable(SELECT_DIALOG_ARGS, mCountryDataList)
                bundle.putInt(SELECT_LEVEL1_DIALOG_ARGS, mCountrySelected)
                bundle.putInt(SELECT_LEVEL2_DIALOG_ARGS, mLevel1Selected as Int)
                mSelectLevel2Dialog.arguments = bundle
                mSelectLevel2Dialog.show(supportFragmentManager, SELECT_LEVEL2_DIALOG_ARGS)
            }
        }

        mSelectLevel2Dialog.setOnLevel2SelectedListener(object : OnLevel2SelectedListener {
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
                Timber.d("check values: %s, %s, %s", mCountrySelected, mLevel1Selected, mLevel2Selected)
                val modelArea = ModelIndicatorLocation(mCountrySelected, mLevel1Selected, mLevel2Selected)
                if (modelArea.validate().isNotEmpty()) {
                    Toasty.error(this, modelArea.validate()).show()
                } else {
                    Timber.d(modelArea.toString())

                    val sb = StringBuilder()
                    sb.append(tvSelectCountry.text).append(", ").append(tvSelectLevel1.text).append(", ").append(tvSelectLevel2.text)
                    val res = sb.toString()

                    val intent = Intent()
                    intent.putExtra(SELECTED_AREA, modelArea)
                    intent.putExtra(SELECTED_AREA_TEXT,  res)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

package org.alertpreparedness.platform.v2.mycountry

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_programme_results.*
import kotlinx.android.synthetic.main.item_programme.*
import org.alertpreparedness.platform.v1.R
import org.alertpreparedness.platform.v1.risk_monitoring.model.CountryJsonData
import org.alertpreparedness.platform.v2.base.BaseActivity
import org.alertpreparedness.platform.v2.models.enums.Country
import org.alertpreparedness.platform.v2.models.enums.CountrySerializer
import org.alertpreparedness.platform.v2.utils.extensions.hide

class ProgrammeResultsActivity : BaseActivity<ProgrammeResultsViewModel>() {
    companion object {
        const val COUNTRY_KEY = "COUNTRY_KEY"
        const val LEVEL_1_KEY = "LEVEL_1_KEY"
        const val LEVEL_2_KEY = "LEVEL_2_KEY"
        const val LOCATION_DATA_KEY = "LOCATION_DATA_KEY"
    }
    private lateinit var country: Country
    private var level1: Int? = null
    private var level2: Int? = null
    private lateinit var adapter: ProgrammeResultsAdapter
    private lateinit var countryData: CountryJsonData

    override fun viewModelClass(): Class<ProgrammeResultsViewModel> {
        return ProgrammeResultsViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_programme_results
    }


    override fun initViews() {
        super.initViews()

        setToolbarTitle(R.string.programme_results)
        showBackButton(drawable=R.drawable.ic_close_white_24dp)

        adapter = ProgrammeResultsAdapter(this, countryData)
        rvResults.adapter = adapter
        rvResults.layoutManager = LinearLayoutManager(this)
        rvResults.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        val level1 = if(level1 != null && countryData.levelOneValues != null) countryData.levelOneValues!!.first { it.id == level1 } else null
        val level2 = if(level2 != null && level1?.levelTwoValues != null) level1.levelTwoValues.first { it.id == level2 } else null

        tvResultsFor.text = getString(R.string.programme_results_for, listOfNotNull(
                getString(country.string),
                level1?.value,
                level2?.value
        ).joinToString(", "))


    }

    override fun arguments(bundle: Bundle) {
        country = CountrySerializer.jsonToEnum(bundle.getInt(COUNTRY_KEY))!!
        level1 = bundle.getInt(LEVEL_1_KEY, -1)
        if(level1 == -1) level1 = null

        level2 = bundle.getInt(LEVEL_2_KEY, -1)
        if(level2 == -1) level2 = null

        @Suppress("UNCHECKED_CAST")
        countryData = bundle.getSerializable(LOCATION_DATA_KEY) as CountryJsonData
    }

    override fun observeViewModel() {
        viewModel.searchArea(country, level1, level2)

        disposables += viewModel.results()
                .subscribe {
                    adapter.updateItems(LinkedHashMap(it.toMutableMap()))
                    progressBar.hide()
                }
    }
}
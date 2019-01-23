package org.alertpreparedness.platform.v1.risk_monitoring.model

import java.io.Serializable

/**
 * Created by fei on 10/11/2017.
 */

data class CountryJsonData(var countryId:Int, val levelOneValues: List<LevelOneValuesItem>? = null) : Serializable
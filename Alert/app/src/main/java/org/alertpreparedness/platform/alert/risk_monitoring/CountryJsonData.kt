package org.alertpreparedness.platform.alert.risk_monitoring

import java.io.Serializable

/**
 * Created by fei on 10/11/2017.
 */

data class CountryJsonData(var countryId:Int, val levelOneValues: List<LevelOneValuesItem>? = null) : Serializable
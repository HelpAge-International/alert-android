package org.alertpreparedness.platform.alert.risk_monitoring.model

import java.io.Serializable

data class LevelOneValuesItem(val levelTwoValues: List<LevelTwoValuesItem>? = null,
                              val id: Int = 0,
                              val value: String = "") : Serializable
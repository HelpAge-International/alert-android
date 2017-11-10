package org.alertpreparedness.platform.alert.risk_monitoring

data class LevelOneValuesItem(val levelTwoValues: List<LevelTwoValuesItem>? = null,
                              val id: Int = 0,
                              val value: String = "")
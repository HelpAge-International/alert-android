package org.alertpreparedness.platform.v1.risk_monitoring.model

/**
 * Created by fei on 24/11/2017.
 */
data class ModelLog(val id:String? = null, val addedBy:String = "", val content:String = "", val timeStamp:Long = 0.toLong(), val triggerAtCreation:Int = 0, var addedByName:String? = null)
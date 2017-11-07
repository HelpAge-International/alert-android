package org.alertpreparedness.platform.alert.risk_monitoring

/**
 * Created by fei on 07/11/2017.
 */
data class ModelIndicator(val id:String, val hazardScenario:ModelHazard, val triggerSelected:Int,
                          val name:String, val assignee:String, val geoLocation:Int,
                          val updatedAt:Long, val dueDate:Long, val source: List<String>, val trigger:List<ModelTrigger>)

data class ModelTrigger(val durationType:String, val frequencyValue:Int, val triggerValue:String)

//public id: string;
//public category: HazardScenario = 0;
//public triggerSelected: number;
//public name: string = '';
//public source: any[] = [];
//public assignee: string;
//public geoLocation: GeoLocation;
//public affectedLocation: any[] = [];
//public trigger: any[] = [];
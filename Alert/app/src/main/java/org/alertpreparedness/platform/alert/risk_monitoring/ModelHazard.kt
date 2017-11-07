package org.alertpreparedness.platform.alert.risk_monitoring

/**
 * Created by fei on 07/11/2017.
 */
data class ModelHazard(val id:String, val hazardScenario:Int, val isActive:Boolean, val isSeasonal:Boolean, val risk:Int, val timeCreated:Long, val otherName:String) {

}

//public id: string;
//
//public category: number;
//public isSeasonal: boolean;
//public location: Map<number, number>;
//public risk: number;
//public hazardType: string;
//public seasons = [];
//public isActive: boolean;
//public hazardScenario: string;
//public otherName: string;
//public displayName: string;

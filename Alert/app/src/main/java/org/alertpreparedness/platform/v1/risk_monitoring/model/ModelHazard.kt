package org.alertpreparedness.platform.v1.risk_monitoring.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by fei on 07/11/2017.
 */
data class ModelHazard(var id:String? = null, val hazardScenario:Int = -2, val isActive:Boolean = true, val isSeasonal:Boolean = false, val risk:Int = 10, var timeCreated:Long = 0.toLong(), val otherName:String? = null, val key:String? = null):Parcelable {


    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readInt(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readString()) {
    }

    fun validateModel() : String  = when {
        hazardScenario == -2 && key == null -> "Hazard is not valid"
        else -> ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeInt(hazardScenario)
        parcel.writeByte(if (isActive) 1 else 0)
        parcel.writeByte(if (isSeasonal) 1 else 0)
        parcel.writeInt(risk)
        parcel.writeLong(timeCreated)
        parcel.writeString(otherName)
        parcel.writeString(key)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ModelHazard> {
        override fun createFromParcel(parcel: Parcel): ModelHazard {
            return ModelHazard(parcel)
        }

        override fun newArray(size: Int): Array<ModelHazard?> {
            return arrayOfNulls(size)
        }
    }

}

data class ModelHazardCountryContext(val key:String = "countryContext")

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

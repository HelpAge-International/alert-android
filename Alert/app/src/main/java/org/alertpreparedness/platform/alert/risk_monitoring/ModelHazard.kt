package org.alertpreparedness.platform.alert.risk_monitoring

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by fei on 07/11/2017.
 */
data class ModelHazard(val id:String, val hazardScenario:Int, val isActive:Boolean, val isSeasonal:Boolean, val risk:Int, val timeCreated:Long, val otherName:String):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readInt(),
            parcel.readLong(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeInt(hazardScenario)
        parcel.writeByte(if (isActive) 1 else 0)
        parcel.writeByte(if (isSeasonal) 1 else 0)
        parcel.writeInt(risk)
        parcel.writeLong(timeCreated)
        parcel.writeString(otherName)
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

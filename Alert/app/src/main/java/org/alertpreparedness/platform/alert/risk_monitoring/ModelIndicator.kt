package org.alertpreparedness.platform.alert.risk_monitoring

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by fei on 07/11/2017.
 */
data class ModelIndicator(val id: String?, var hazardScenario: ModelHazard, val triggerSelected: Int,
                          var name: String, var assignee: String?, var geoLocation: Int,
                          var updatedAt: Long, var dueDate: Long, var source: List<ModelSource>, var trigger: List<ModelTrigger>, val networkId: String?, val agencyId:String?, val countryOfficeId:String?) : Parcelable {

    constructor() : this(null, ModelHazard(), 0, "", null, -1, 0, 0, listOf(), listOf(), null, null, null)



    fun validateModel() : String = when {
        name.isEmpty() -> "Indicator name can not be empty!"
        geoLocation == -1 -> "Location can not be empty!"
        dueDate == 0.toLong() -> "Due date can not be empty"
        updatedAt == 0.toLong() -> "Update time can not be empty"
        source.isEmpty() ->"No source added for this indicator!"
        else -> ""
    }

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readParcelable(ModelHazard::class.java.classLoader),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.createTypedArrayList(ModelSource),
            parcel.createTypedArrayList(ModelTrigger),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeParcelable(hazardScenario, flags)
        parcel.writeInt(triggerSelected)
        parcel.writeString(name)
        parcel.writeString(assignee)
        parcel.writeInt(geoLocation)
        parcel.writeLong(updatedAt)
        parcel.writeLong(dueDate)
        parcel.writeTypedList(source)
        parcel.writeTypedList(trigger)
        parcel.writeString(networkId)
        parcel.writeString(agencyId)
        parcel.writeString(countryOfficeId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ModelIndicator> {
        override fun createFromParcel(parcel: Parcel): ModelIndicator {
            return ModelIndicator(parcel)
        }

        override fun newArray(size: Int): Array<ModelIndicator?> {
            return arrayOfNulls(size)
        }
    }
}

/************************************************************************************************************************/

data class ModelTrigger(val durationType: String, val frequencyValue: Int, val triggerValue: String) : Parcelable {

    constructor() : this("", -1, "")

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(durationType)
        parcel.writeInt(frequencyValue)
        parcel.writeString(triggerValue)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ModelTrigger> {
        override fun createFromParcel(parcel: Parcel): ModelTrigger {
            return ModelTrigger(parcel)
        }

        override fun newArray(size: Int): Array<ModelTrigger?> {
            return arrayOfNulls(size)
        }
    }

    fun validateModel(): Boolean {
        var validation = true
        when {
            durationType.isEmpty() -> validation = false
            frequencyValue == -1 -> validation = false
            triggerValue.isEmpty() -> validation = false
        }
        return validation
    }
}

/************************************************************************************************************************/

data class ModelSource(val sourceName: String, val sourceLink: String?) : Parcelable {

    constructor() : this("", null)

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sourceName)
        parcel.writeString(sourceLink)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ModelSource> {
        override fun createFromParcel(parcel: Parcel): ModelSource {
            return ModelSource(parcel)
        }

        override fun newArray(size: Int): Array<ModelSource?> {
            return arrayOfNulls(size)
        }
    }

    fun validateModel(): Boolean {
        if (sourceName.isEmpty()) {
            return false
        }
        return true
    }
}

//public id: string;
//public category: HazardScenario = 0;
//public triggerSelected: number;
//public name: string = '';
//public source: any[] = [];
//public assignee: string;
//public geoLocation: GeoLocation;
//public affectedLocation: any[] = [];
//public trigger: any[] = [];
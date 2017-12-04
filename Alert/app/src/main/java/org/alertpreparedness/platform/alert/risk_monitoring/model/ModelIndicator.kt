package org.alertpreparedness.platform.alert.risk_monitoring.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by fei on 07/11/2017.
 */
data class ModelIndicator(val id: String?, var hazardScenario: ModelHazard, val triggerSelected: Int,
                          var name: String, var assignee: String?, var geoLocation: Int,
                          var updatedAt: Long, var dueDate: Long, var source: List<ModelSource>, var trigger: List<ModelTrigger>, val networkId: String?, val agencyId: String?, val countryOfficeId: String?,
                          var affectedLocation: List<ModelIndicatorLocation>?, var gps: ModelGps?, val category: Int = 0, val networkName: String? = null) : Parcelable {


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
            parcel.readString(),
            parcel.createTypedArrayList(ModelIndicatorLocation),
            parcel.readParcelable(ModelGps::class.java.classLoader),
            parcel.readInt(),
            parcel.readString()) {
    }

    constructor() : this(null, ModelHazard(), 0, "", null, -1, 0, 0, listOf(), listOf(), null, null, null, null, null)

    fun validateModel(): String = when {
        name.isEmpty() -> "Indicator name can not be empty!"
        geoLocation == -1 -> "Location can not be empty!"
        dueDate == 0.toLong() -> "Due date can not be empty"
        updatedAt == 0.toLong() -> "Update time can not be empty"
        source.isEmpty() -> "No source added for this indicator!"
        else -> ""
    }

    fun validateLocation(): String = when {
        affectedLocation == null || affectedLocation?.size == 0 -> {
            "Area can not be empty!"
        }
        else -> {
            ""
        }
    }

    fun validateGps(): String = when {
        gps == null || gps!!.latitude.isEmpty() || gps!!.longitude.isEmpty() -> {
            "Current location is not available!"
        }
        else -> {
            ""
        }
    }

    fun resetLevels() {
        affectedLocation?.forEach {
            when {
                it.level1 == -1 -> {
                    it.level1 = null
                    it.level2 = null
                }
                it.level2 == -1 -> it.level2 = null
                else -> {
                }
            }
        }
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
        parcel.writeTypedList(affectedLocation)
        parcel.writeParcelable(gps, flags)
        parcel.writeInt(category)
        parcel.writeString(networkName)
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

data class ModelTrigger(val durationType: String, val frequencyValue: String, val triggerValue: String) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    constructor() : this("", "", "")


    fun validateModel(): Boolean {
        var validation = true
        when {
            durationType.isEmpty() -> validation = false
            frequencyValue.isEmpty() -> validation = false
            triggerValue.isEmpty() -> validation = false
        }
        return validation
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(durationType)
        parcel.writeString(frequencyValue)
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
}

/************************************************************************************************************************/

data class ModelSource(val name: String, val link: String?) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString()) {
    }

    constructor() : this("", null)


    fun validateModel(): Boolean {
        if (name.isEmpty()) {
            return false
        }
        return true
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(link)
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
}

/************************************************************************************************************************/

data class ModelIndicatorLocation(val country: Int = -1, var level1: Int? = null, var level2: Int? = null) : Parcelable {


    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int) {
    }

    fun validate(): String = when {
        country == -1 -> {
            "Country can not be empty, please select!"
        }
        else -> {
            ""
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(country)
        parcel.writeValue(level1)
        parcel.writeValue(level2)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ModelIndicatorLocation> {
        override fun createFromParcel(parcel: Parcel): ModelIndicatorLocation {
            return ModelIndicatorLocation(parcel)
        }

        override fun newArray(size: Int): Array<ModelIndicatorLocation?> {
            return arrayOfNulls(size)
        }
    }

}

/************************************************************************************************************************/

data class ModelGps(val city: String? = null, val country: String? = null, var address: String? = null, val latitude: String = "", val longitude: String = "") : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(city)
        parcel.writeString(country)
        parcel.writeString(address)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ModelGps> {
        override fun createFromParcel(parcel: Parcel): ModelGps {
            return ModelGps(parcel)
        }

        override fun newArray(size: Int): Array<ModelGps?> {
            return arrayOfNulls(size)
        }
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
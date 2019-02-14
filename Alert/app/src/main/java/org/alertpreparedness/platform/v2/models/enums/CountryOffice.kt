package org.alertpreparedness.platform.v2.models.enums

import org.alertpreparedness.platform.v2.models.BaseModel
import org.alertpreparedness.platform.v2.models.ClockSetting

class CountryOffice(
        val addressLine1: String,
        val addressLine2: String,
        val addressLine3: String,
        val adminId: String,
        val city: String,
        val isActive: Boolean,
        val phone: String,
        val postCode: String
) : BaseModel() {

    @Transient
    lateinit var mPreparednessClockSetting: ClockSetting
    @Transient
    lateinit var mResponsePlanClockSetting: ClockSetting
}
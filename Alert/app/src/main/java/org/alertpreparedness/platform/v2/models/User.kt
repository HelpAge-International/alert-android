package org.alertpreparedness.platform.v2.models

import org.alertpreparedness.platform.v2.models.enums.Title

class User(
        val addressLine1: String,
        val addressLine2: String,
        val addressLine3: String,
        val city: String,
        val country: Int,
        val email: String,
        val firstName: String,
        val lastName: String,
        val latestCoCAgreed: Boolean,
        val latestToCAgreed: Boolean,
        val phone: String,
        val postCode: String,
        val title: Title
) : BaseModel() {

    lateinit var countryId: String
    @Transient
    lateinit var userType: UserType
    @Transient
    lateinit var agencyAdminId: String
    @Transient
    lateinit var systemAdminId: String
}
package org.alertpreparedness.platform.v2.models

import org.alertpreparedness.platform.v2.models.enums.Title

class User(
        addressLine1: String,
        addressLine2: String,
        addressLine3: String,
        city: String,
        country: Int,
        email: String,
        firstName: String,
        lastName: String,
        latestCoCAgreed: Boolean,
        latestToCAgreed: Boolean,
        phone: String,
        postCode: String,
        title: Title
) : UserPublic(addressLine1, addressLine2, addressLine3, city, country, email, firstName, lastName, latestCoCAgreed,
        latestToCAgreed, phone, postCode, title) {

    lateinit var countryId: String
    @Transient
    lateinit var userType: UserType
    @Transient
    lateinit var agencyAdminId: String
    @Transient
    lateinit var systemAdminId: String

    override fun toString(): String {
        return "User(addressLine1='$addressLine1', addressLine2='$addressLine2', addressLine3='$addressLine3', city='$city', country=$country, email='$email', firstName='$firstName', lastName='$lastName', latestCoCAgreed=$latestCoCAgreed, latestToCAgreed=$latestToCAgreed, phone='$phone', postCode='$postCode', title=$title, countryId='$countryId', userType=$userType, agencyAdminId='$agencyAdminId', systemAdminId='$systemAdminId')"
    }
}
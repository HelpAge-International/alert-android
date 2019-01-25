package org.alertpreparedness.platform.v2.models

class User(
        val userType: UserType,
        val countryId: String,
        val agencyAdminId: String,
        val systemAdminId: String,
        id: String
): BaseModel(id) {

    override fun toString(): String {
        return "User(id=$id, userType=$userType, countryId='$countryId', agencyAdminId='$agencyAdminId', systemAdminId='$systemAdminId')"
    }
}
package org.alertpreparedness.platform.v2.models

import org.alertpreparedness.platform.v2.models.enums.Country

class Agency(
        val addressLine1: String,
        val addressLine2: String,
        val addressLine3: String,
        val adminId: String,
        val city: String,
        val country: Country,
        val currency: Int,
        val isActive: Boolean,
        val isDonor: Boolean,
        val isGlobalAgency: Boolean,
        val logoPath: String,
        val name: String,
        val phone: String,
        val postCode: String,
        val website: String
) : BaseModel()
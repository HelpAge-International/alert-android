package org.alertpreparedness.platform.v2.models

import org.alertpreparedness.platform.v2.models.enums.Country

class Area(
        val country: Country,
        val level1: Int?,
        val level2: Int?
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Area) return false

        if (country != other.country) return false
        if (level1 != other.level1) return false
        if (level2 != other.level2) return false

        return true
    }

    override fun hashCode(): Int {
        var result = country.hashCode()
        result = 31 * result + (level1 ?: 0)
        result = 31 * result + (level2 ?: 0)
        return result
    }
}
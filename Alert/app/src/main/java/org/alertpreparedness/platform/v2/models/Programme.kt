package org.alertpreparedness.platform.v2.models

import com.google.gson.annotations.SerializedName
import org.alertpreparedness.platform.v2.models.enums.Country
import org.alertpreparedness.platform.v2.models.enums.Sector
import org.joda.time.DateTime

class Programme(
        val agencyId: String,
        val level1: Int?,
        val level2: Int?,
        val sector: Sector,
        val toDate: DateTime,
        val updatedAt: DateTime,
        val toWho: String,
        val what: String,
        //when is a hard keyword in kotlin
        @SerializedName("when")
        val time: DateTime,
        val where: Country
) : BaseModel() {

        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Programme

                if (agencyId != other.agencyId) return false
                if (level1 != other.level1) return false
                if (level2 != other.level2) return false
                if (sector != other.sector) return false
                if (toDate != other.toDate) return false
                if (updatedAt != other.updatedAt) return false
                if (toWho != other.toWho) return false
                if (what != other.what) return false
                if (time != other.time) return false
                if (where != other.where) return false

                return true
        }

        override fun hashCode(): Int {
                var result = agencyId.hashCode()
                result = 31 * result + (level1 ?: 0)
                result = 31 * result + (level2 ?: 0)
                result = 31 * result + sector.hashCode()
                result = 31 * result + toDate.hashCode()
                result = 31 * result + updatedAt.hashCode()
                result = 31 * result + toWho.hashCode()
                result = 31 * result + what.hashCode()
                result = 31 * result + time.hashCode()
                result = 31 * result + where.hashCode()
                return result
        }
}
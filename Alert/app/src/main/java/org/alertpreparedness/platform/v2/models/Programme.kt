package org.alertpreparedness.platform.v2.models

import com.google.gson.annotations.SerializedName
import org.alertpreparedness.platform.v2.models.enums.Sector
import org.joda.time.DateTime

class Programme(
        val agencyId: String,
        val level1: Int,
        val level2: Int,
        val sector: Sector,
        val toDate: DateTime,
        val updatedAt: DateTime,
        val toWho: String,
        val what: String,
        //when is a hard keyword in kotlin
        @SerializedName("when")
        val time: DateTime,
        val where: Country
) : BaseModel()
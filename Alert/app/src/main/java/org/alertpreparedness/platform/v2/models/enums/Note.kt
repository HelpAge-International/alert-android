package org.alertpreparedness.platform.v2.models.enums

import org.alertpreparedness.platform.v2.models.BaseModel
import org.joda.time.DateTime

class Note(
        val content: String,
        val time: DateTime,
        val uploadedBy: String
) : BaseModel()
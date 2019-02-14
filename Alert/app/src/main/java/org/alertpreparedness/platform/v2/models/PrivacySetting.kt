package org.alertpreparedness.platform.v2.models

import org.alertpreparedness.platform.v2.models.enums.Privacy

class PrivacySetting(
        val privacy: Privacy,
        val status: Boolean
) : BaseModel()
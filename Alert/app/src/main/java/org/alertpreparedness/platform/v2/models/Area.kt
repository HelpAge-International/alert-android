package org.alertpreparedness.platform.v2.models

import org.alertpreparedness.platform.v2.models.enums.Country

class Area(
        val country: Country,
        val level1: Int,
        val level2: Int
)
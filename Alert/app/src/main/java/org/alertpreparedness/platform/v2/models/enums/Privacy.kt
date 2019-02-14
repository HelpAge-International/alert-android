package org.alertpreparedness.platform.v2.models.enums

import org.alertpreparedness.platform.v2.utils.EnumSerializer

enum class Privacy(val value: Int) {
    PUBLIC(0),
    PRIVATE(1),
    NETWORK(2)
}

object PrivacySerializer : EnumSerializer<Privacy>(
        Privacy::class.java,
        { it?.value }
)



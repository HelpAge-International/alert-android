package org.alertpreparedness.platform.v2.models.enums

import org.alertpreparedness.platform.v2.utils.EnumSerializer

enum class Title(val value: Int) {
    MR(0),
    MRS(1),
    MISS(2),
    MS(3),
    DR(4),
    PROF(5)
}

object TitleSerializer : EnumSerializer<Title>(
        Title::class.java,
        { it?.value }
)

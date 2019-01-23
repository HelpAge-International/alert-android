package org.alertpreparedness.platform.v2.utils.extensions

import android.content.Context
import android.util.TypedValue

fun Number.toDp(context: Context): Float {
    return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            context.resources.displayMetrics
    )

}

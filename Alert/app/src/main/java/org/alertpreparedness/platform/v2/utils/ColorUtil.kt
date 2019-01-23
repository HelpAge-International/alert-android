package org.alertpreparedness.platform.v2.utils

import android.graphics.Color
import android.util.Log
import androidx.annotation.ColorInt
import java.text.ParseException

class ColorUtil {
    companion object {

        @ColorInt
        fun darken(@ColorInt color: Int): Int {
            return manipulate(color, 0.8f)
        }

        @ColorInt
        fun lighten(@ColorInt color: Int): Int {
            return manipulate(color, 1.2f)
        }

        @ColorInt
        fun manipulate(@ColorInt color: Int, factor: Float): Int {
            val a = Color.alpha(color)
            val r = Math.round(Color.red(color) * factor)
            val g = Math.round(Color.green(color) * factor)
            val b = Math.round(Color.blue(color) * factor)
            return Color.argb(a,
                    Math.min(r, 255),
                    Math.min(g, 255),
                    Math.min(b, 255))
        }

        @ColorInt
        fun parse(color: String?): Int {
            return try {
                Color.parseColor(color)
            } catch (e: ParseException) {
                Log.d("KidzDiretc", "Colour $color cannot be parsed")
                Color.WHITE
            }
        }
    }
}
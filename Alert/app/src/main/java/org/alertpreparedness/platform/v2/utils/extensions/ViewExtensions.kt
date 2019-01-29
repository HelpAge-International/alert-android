package org.alertpreparedness.platform.v2.utils.extensions

import android.view.View

fun View.show(show: Boolean = true){
    visibility = if (show) View.VISIBLE else View.GONE
}

fun View.hide(){
    visibility = View.GONE
}

fun View.invisible(){
    visibility = View.INVISIBLE
}
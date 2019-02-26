package org.alertpreparedness.platform.v2.utils.extensions

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText

fun View.show(show: Boolean = true){
    visibility = if (show) View.VISIBLE else View.GONE
}

fun View.hide(){
    visibility = View.GONE
}

fun View.invisible(){
    visibility = View.INVISIBLE
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int,
                count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

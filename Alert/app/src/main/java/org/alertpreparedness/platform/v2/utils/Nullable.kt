package org.alertpreparedness.platform.v2.utils

import io.reactivex.Observable

data class Nullable<T>(val value: T? = null) {
    fun isNull(): Boolean {
        return value == null
    }

    fun isNotNull(): Boolean {
        return value != null
    }
}

fun <T> Observable<Nullable<T>>.filterNull(): Observable<T> {
    return filter {
        it.value != null
    }
            .map {
                it.value!!
            }
}
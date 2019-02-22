package org.alertpreparedness.platform.v2.utils

sealed class ListOperation<T>
data class Add<T>(val value: T) : ListOperation<T>()
data class Remove<T>(val value: T) : ListOperation<T>()

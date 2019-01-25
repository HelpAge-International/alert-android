package org.alertpreparedness.platform.v2.models
data class Path(val items: List<String>, val pointer: Int = 0){
    fun id() = items[pointer]
    fun parent() = copy(pointer = pointer + 1)
    fun child() = copy(pointer = pointer - 1)
    fun parentCount() = items.size - (pointer + 1)
    fun childCount() = (items.size - 1) - parentCount()
}
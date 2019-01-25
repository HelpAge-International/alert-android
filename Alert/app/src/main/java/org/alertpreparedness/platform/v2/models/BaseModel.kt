package org.alertpreparedness.platform.v2.models

open class BaseModel(){
    lateinit var id: String

    //Most of the time this needs to be called without the ref variable, but the ref variable cant be nullbale
    //Therefore we have this secondary constructor.
    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(key: String) : this() {
        this.id = key
    }
}
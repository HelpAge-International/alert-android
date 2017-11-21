package org.alertpreparedness.platform.alert.risk_monitoring

import java.io.Serializable

/**
 * Created by fei on 16/11/2017.
 */
data class ModelUserPublic(var id: String = "", val firstName: String = "", val lastName: String = "", val phone: String = "",
                           val title: Any = 0, val email: String = "", val addressLine1: String? = "", val addressLine2: String? = "", val addressLine3: String? = "",
                           val country: Int? = -1, val city: Any? = "", val postCode: Any? = "", val language:String? = "en"):Serializable

//id: string;
//firstName: string;
//lastName: string;
//phone: string;
//title: number;
//email: string;
//addressLine1: string;
//addressLine2: string;
//addressLine3: string;
//country: number;
//city: string;
//postCode: string;
package org.alertpreparedness.platform.alert.risk_monitoring.model

/**
 * Created by fei on 15/11/2017.
 */
data class ModelNetwork(val networkAdminId: String, val name: String, val isActive: Boolean, val logoPath: String?,
                        val addressLine1: String?, val addressLine2: String?, val addressLine3: String?, val countryId: Int?,
                        val city: String?, val postcode: String?, val telephone: String?, val websiteAddress: String?,
                        val isGlobal: Boolean, val countryCode: Int?, val clockSettings: Any?, val responsePlanSettings: Any?,
                        var id: String, val agencies: Any?, val leadAgencyId: String?) {
    constructor():this("","",false,null,null,null,null,null,null,null,null,null,true,null,null,null,"",null,null)
}

//public networkAdminId: string;
//public name: string;
//public isActive: boolean;
//public logoPath: string;
//public isInitialisedNetwork: boolean;
//public addressLine1: string;
//public addressLine2: string;
//public addressLine3: string;
//public countryId: number;
//public city: string;
//public postcode: string;
//public telephone: string;
//public websiteAddress: string;
//public isGlobal: boolean;
//public countryCode: number;
//public clockSettings:{} = {};
//public responsePlanSettings:{} = {};
//public id:string;
//public agencies?: string[];
//public leadAgencyId?: string;

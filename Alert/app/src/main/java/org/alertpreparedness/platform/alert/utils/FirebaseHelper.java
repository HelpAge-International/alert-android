package org.alertpreparedness.platform.alert.utils;

import com.google.firebase.database.DatabaseReference;

/**
 * ==============================
 * Created by Fei
 * Dated: 19/01/2017
 * Email: fei@rolleragency.co.uk
 * Copyright Roller Agency
 * ==============================
 */

public class FirebaseHelper {

    public static DatabaseReference getHazardsRef(String appStatus, String countryId) {
        return AppUtils.getDatabase().getReference(appStatus).child(Constants.HAZARD_PATH).child(countryId);
    }

    public static DatabaseReference getIndicatorsRef(String appStatus, String hazardId) {
        return AppUtils.getDatabase().getReference(appStatus).child(Constants.INDICATOR_PATH).child(hazardId);
    }

    /******employee references*****/
//    public static DatabaseReference getEmployeePublicRef(String appStatus, String companyKey, String uid) {
//        return AppUtils.getDatabase().getReference(appStatus).child(Constants.COMPANY_DATA).child(companyKey).child(Constants.EMPLOYEE_PUBLIC_DATA).child(uid);
//    }

}

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
        return AppUtils.getDatabase().getReference(appStatus).child("hazard").child(countryId);
    }

    public static DatabaseReference getHazardsOtherNameRef(String appStatus, String nameId) {
        return AppUtils.getDatabase().getReference(appStatus).child("hazardOther").child(nameId);
    }

    public static DatabaseReference getIndicatorsRef(String appStatus, String hazardId) {
        return AppUtils.getDatabase().getReference(appStatus).child("indicator").child(hazardId);
    }

    public static DatabaseReference getNetworkMapRef(String appStatus, String agencyId, String countryId) {
        return AppUtils.getDatabase().getReference(appStatus).child("countryOffice").child(agencyId).child(countryId).child("networks");
    }

    public static DatabaseReference getNetworkDetail(String appStatus, String networkId) {
        return AppUtils.getDatabase().getReference(appStatus).child("network").child(networkId);
    }

    public static DatabaseReference getStaffCountry(String appStatus, String countryId) {
        return AppUtils.getDatabase().getReference(appStatus).child("staff").child(countryId);
    }

    public static DatabaseReference getUserDetail(String appStatus, String userId) {
        return AppUtils.getDatabase().getReference(appStatus).child("userPublic").child(userId);
    }

    public static DatabaseReference checkConnectedRef() {
        return AppUtils.getDatabase().getReference(".info/connected");
    }

    /******employee references*****/
//    public static DatabaseReference getEmployeePublicRef(String appStatus, String companyKey, String uid) {
//        return AppUtils.getDatabase().getReference(appStatus).child(Constants.COMPANY_DATA).child(companyKey).child(Constants.EMPLOYEE_PUBLIC_DATA).child(uid);
//    }

}

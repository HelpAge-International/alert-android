package org.alertpreparedness.platform.alert.realm;

import org.alertpreparedness.platform.alert.model.User;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Tj on 04/01/2018.
 */

public class UserRealm extends RealmObject {

    @PrimaryKey
    private String userId;
    private String agencyAdmin;
    private String systemAdmin;
    private String countryId;
    private Integer userType;
    private boolean isCountryDirector;

    public UserRealm(){}

    public UserRealm(String userId, String agencyAdmin, String systemAdmin, String countryId, Integer userType, boolean isCountryDirector) {
        this.userId = userId;
        this.agencyAdmin = agencyAdmin;
        this.systemAdmin = systemAdmin;
        this.countryId = countryId;
        this.userType = userType;
        this.isCountryDirector = isCountryDirector;
    }

    public String getAgencyAdmin() {
        return agencyAdmin;
    }

    public void setAgencyAdmin(String agencyAdmin) {
        this.agencyAdmin = agencyAdmin;
    }

    public String getSystemAdmin() {
        return systemAdmin;
    }

    public void setSystemAdmin(String systemAdmin) {
        this.systemAdmin = systemAdmin;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public boolean isCountryDirector() {
        return isCountryDirector;
    }

    public void setCountryDirector(boolean countryDirector) {
        isCountryDirector = countryDirector;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public User toUser() {
        return new User(userId,userType,agencyAdmin,countryId,systemAdmin,null,null, isCountryDirector);
    }

    public UserRealm getByPrimaryKey(Realm realm, String id) {
        return realm.where(getClass()).equalTo("userId", id).findFirst();
    }
}

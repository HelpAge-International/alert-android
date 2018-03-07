package org.alertpreparedness.platform.alert.realm;

import org.alertpreparedness.platform.alert.model.User;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Tj on 04/01/2018.
 */

public class UserRealm {

    @PrimaryKey
    private String userId;
    private String agencyAdmin;
    private String systemAdmin;
    private String countryId;
    private String localNetworkId;
    private String networkId;
    private String networkCountryId;
    private Integer userType;
    private boolean isCountryDirector;

    public UserRealm(){}

    @Override
    public String toString() {
        return "UserRealm{" +
                "userId='" + userId + '\'' +
                ", agencyAdmin='" + agencyAdmin + '\'' +
                ", systemAdmin='" + systemAdmin + '\'' +
                ", countryId='" + countryId + '\'' +
                ", localNetworkId='" + localNetworkId + '\'' +
                ", networkId='" + networkId + '\'' +
                ", networkCountryId='" + networkCountryId + '\'' +
                ", userType=" + userType +
                ", isCountryDirector=" + isCountryDirector +
                '}';
    }

    public UserRealm(String userId, String agencyAdmin, String systemAdmin, String countryId, String localNetworkId, String networkId, String networkCountryId, Integer userType, boolean isCountryDirector) {
        this.userId = userId;
        this.agencyAdmin = agencyAdmin;
        this.systemAdmin = systemAdmin;
        this.countryId = countryId;
        this.localNetworkId = localNetworkId;
        this.networkId = networkId;
        this.networkCountryId = networkCountryId;
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

    public String getLocalNetworkId() {
        return localNetworkId;
    }

    public void setLocalNetworkId(String localNetworkId) {
        this.localNetworkId = localNetworkId;
    }

    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    public String getNetworkCountryId() {
        return networkCountryId;
    }

    public void setNetworkCountryId(String networkCountryId) {
        this.networkCountryId = networkCountryId;
    }

    public User toUser() {
        return new User(userId,userType,agencyAdmin,countryId,systemAdmin, networkCountryId, localNetworkId, networkId, null, isCountryDirector);
    }

}

package org.alertpreparedness.platform.alert.helper;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.preference.PreferenceManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseCountryOfficeRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseDatabaseRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserId;
import org.alertpreparedness.platform.alert.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.alert.interfaces.AuthCallback;
import org.alertpreparedness.platform.alert.login.activity.LoginScreen;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.notifications.NotificationIdHandler;
import org.alertpreparedness.platform.alert.realm.UserRealm;
import org.alertpreparedness.platform.alert.risk_monitoring.view_model.SelectAreaViewModel;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by faizmohideen on 08/11/2017.
 */

public class UserInfo implements ValueEventListener {
    private static String[] users = {"administratorCountry", "countryDirector", "ert", "ertLeader", "partner"};

    public static final String PREFS_USER = "prefs_user";

    @Inject
    @BaseDatabaseRef
    DatabaseReference database;

    @Inject
    @UserId
    String userId;

    @Inject @BaseDatabaseRef
    DatabaseReference db;

    @Inject @BaseCountryOfficeRef
    DatabaseReference countryOffice;

    @Inject
    @UserPublicRef
    DatabaseReference userPublic;

    @Inject
    Context context;

    private UserAuthenticationListener listener = new UserAuthenticationListener();
    private AuthCallback authCallback;
    private User userObj;
    private ArrayList mCountryDataList;
    private LoginScreen activity;

    public UserInfo() {
        DependencyInjector.applicationcomponent().inject(this);
    }

    public void authUser(final AuthCallback authCallback, String userId) {
        this.userId = userId;
        this.authCallback = authCallback;

        for (String nodeName : users) {
            db = database.child(nodeName);
            db.addListenerForSingleValueEvent(listener);
        }
    }

    public void removeListeners() {
        if(db != null) {
            db.removeEventListener(listener);
        }
    }

    private void saveUser(User user) {
        String serializedUser = new Gson().toJson(user);
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREFS_USER, serializedUser)
                .apply();
    }

    public User getUser() {
        String serializedUser = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREFS_USER, null);

        return new Gson().fromJson(serializedUser, User.class);
    }

    private void populateUser(String nodeName, DataSnapshot userNode) {

        int userType = getUserType(nodeName);

        String agencyAdmin = "";
        String systemAdmin = "";

        if(userNode.child("agencyAdmin").hasChildren()) {
            agencyAdmin = userNode.child("agencyAdmin").getChildren().iterator().next().getKey();
            PreferHelper.putString(context, Constants.AGENCY_ID, agencyAdmin);
            String agencyIDTemp = PreferHelper.getString(context, Constants.AGENCY_ID);
            System.out.println("agencyIDTemp = " + agencyIDTemp);
        }

        if(userNode.child("systemAdmin").hasChildren()) {
            systemAdmin = userNode.child("systemAdmin").getChildren().iterator().next().getKey();
            PreferHelper.putString(context, Constants.SYSTEM_ID, systemAdmin);
        }

        System.out.println("userNode.getRef() = " + userNode.getRef());
        System.out.println("userNode = " + userNode);

        String countryId = userNode.child("countryId").getValue(String.class);

        PreferHelper.putInt(context, Constants.USER_TYPE, userType);

        UserRealm user = new UserRealm(userId, agencyAdmin, systemAdmin, countryId, null, null, null, userType, nodeName.equals("countryDirector"));

        //PreferHelper.putString(context, Constants.COUNTRY_ID, user.getCountryId());

       // userObj = user.toUser();
        //countryOffice.child(user.getAgencyAdmin()).child(user.getCountryId()).addValueEventListener(this);

        getNetworkIDs(user, agencyAdmin, systemAdmin, countryId, userType, nodeName);

    }

    private void getNetworkIDs(UserRealm user, String agencyAdmin, String systemAdmin, String countryId, int userType, String nodeName) {
        System.out.println("countryOffice.chi= " + countryOffice.child(user.getAgencyAdmin()).child(user.getCountryId()).getRef());
        countryOffice.child(user.getAgencyAdmin()).child(user.getCountryId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String localNetworkID = "";
                String networkID = "";
                String networkCountryID = "";

                if(dataSnapshot.child("localNetworks").hasChildren()) {
                    localNetworkID = dataSnapshot.child("localNetworks").getChildren().iterator().next().getKey();
                    PreferHelper.putString(context, Constants.LOCAL_NETWORK_ID, localNetworkID);
                    System.out.println("localNetworkID NN= " + localNetworkID);
                }

                if(dataSnapshot.child("networks").hasChildren()) {
                    networkID = dataSnapshot.child("networks").getChildren().iterator().next().getKey();
                    PreferHelper.putString(context, Constants.NETWORK_ID, networkID);
                    System.out.println("NetworkID NN= " + networkID);

                    networkCountryID = (String) dataSnapshot.child("networks").child(networkID).child("networkCountryId").getValue();
                    PreferHelper.putString(context, Constants.NETWORK_COUNTRY_ID, networkCountryID);
                    System.out.println("NetworkCountryID NN= " + networkCountryID);
                }

                UserRealm user = new UserRealm(userId, agencyAdmin, systemAdmin, countryId, localNetworkID, networkID, networkCountryID, userType, nodeName.equals("countryDirector"));

                PreferHelper.putString(context, Constants.COUNTRY_ID, user.getCountryId());

                userObj = user.toUser();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        countryOffice.child(user.getAgencyAdmin()).child(user.getCountryId()).addValueEventListener(this);

    }

    private int getUserType(String node) {
        switch (node) {
            case "administratorCountry":
                return Constants.CountryAdmin;
            case "countryDirector":
                return Constants.CountryDirector;
            case "ert":
                return Constants.Ert;
            case "ertLeader":
                return Constants.ErtLeader;
            case "partner":
                return Constants.PartnerUser;
            default:
                return -1;
        }
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "database=" + database +
                ", userId='" + userId + '\'' +
                ", listener=" + listener +
                ", authCallback=" + authCallback +
                ", db=" + db +
                ", User=" + getUser() +
                ", context=" + context +
                '}';
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        SelectAreaViewModel mViewModel = ViewModelProviders.of(activity).get(SelectAreaViewModel.class);

        mViewModel.getCountryJsonDataLive().observe(() -> activity.getLifecycle(), countryJsonData -> {

            if (countryJsonData != null) {
                mCountryDataList = new ArrayList<>(countryJsonData);

                if (mCountryDataList.size() == 248) {
                    String country = Constants.COUNTRIES[((int) (long) dataSnapshot.child("location").getValue())];
                    userObj.setCountryName(country);
                    userObj.setCountryListId(((int) (long) dataSnapshot.child("location").getValue()));
                    saveUser(userObj);
                    if(authCallback != null) {
                        authCallback.onUserAuthorized(userObj);
                    }
                }
            }

        });

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public void setActivity(LoginScreen activity) {
        this.activity = activity;
    }

    private class UserAuthenticationListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.child(userId).exists()) {
                DataSnapshot userNode = dataSnapshot.child(userId);
                populateUser(dataSnapshot.getKey(), userNode);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            System.out.println("databaseError = " + databaseError);
        }

    }
}


package org.alertpreparedness.platform.alert.helper;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.alertpreparedness.platform.alert.AlertApplication;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyBaseRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseAlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseCountryOfficeRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseDatabaseRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserId;
import org.alertpreparedness.platform.alert.interfaces.AuthCallback;
import org.alertpreparedness.platform.alert.login.activity.LoginScreen;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.realm.UserRealm;
import org.alertpreparedness.platform.alert.risk_monitoring.service.NetworkService;
import org.alertpreparedness.platform.alert.risk_monitoring.view_model.SelectAreaViewModel;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.DBListener;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;

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
    Context context;

    private UserAuthenticationListener listener = new UserAuthenticationListener();
    private AuthCallback authCallback;
    private User userObj;
    private ArrayList mCountryDataList;
    private LoginScreen activity;

    public UserInfo() {
        DependencyInjector.applicationComponent().inject(this);
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
            PreferHelper.putString(AlertApplication.getContext(), Constants.AGENCY_ID, agencyAdmin);
            String agencyIDTemp = PreferHelper.getString(AlertApplication.getContext(), Constants.AGENCY_ID);
            System.out.println("agencyIDTemp = " + agencyIDTemp);
        }

        if(userNode.child("systemAdmin").hasChildren()) {
            systemAdmin = userNode.child("systemAdmin").getChildren().iterator().next().getKey();
            PreferHelper.putString(AlertApplication.getContext(), Constants.SYSTEM_ID, systemAdmin);
        }

        System.out.println("userNode.getRef() = " + userNode.getRef());
        System.out.println("userNode = " + userNode);

        String countryId = userNode.child("countryId").getValue(String.class);

        PreferHelper.putInt(AlertApplication.getContext(), Constants.USER_TYPE, userType);

        UserRealm user = new UserRealm(userId, agencyAdmin, systemAdmin, countryId, userType, nodeName.equals("countryDirector"));
        userObj = user.toUser();
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
        System.out.println("coutnryofficedataSnapshot = [" + dataSnapshot + "]");

        SelectAreaViewModel mViewModel = ViewModelProviders.of(activity).get(SelectAreaViewModel.class);

        mViewModel.getCountryJsonDataLive().observe(() -> activity.getLifecycle(), countryJsonData -> {

            if (countryJsonData != null) {
                mCountryDataList = new ArrayList<>(countryJsonData);

                if (mCountryDataList.size() > 240) {
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


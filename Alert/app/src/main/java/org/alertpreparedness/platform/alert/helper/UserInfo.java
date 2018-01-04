package org.alertpreparedness.platform.alert.helper;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.alertpreparedness.platform.alert.AlertApplication;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseAlertRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseDatabaseRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserId;
import org.alertpreparedness.platform.alert.interfaces.AuthCallback;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.realm.UserRealm;
import org.alertpreparedness.platform.alert.risk_monitoring.service.NetworkService;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.DBListener;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;

/**
 * Created by faizmohideen on 08/11/2017.
 */

public class UserInfo {
    private static String[] users = {"administratorCountry", "countryDirector", "ert", "ertLeader", "partner"};

    public static final String PREFS_USER = "prefs_user";

    @Inject
    @BaseDatabaseRef
    DatabaseReference database;

    @Inject
    @UserId
    String userId;

    @Inject
    Realm realm;

    @Inject
    @AgencyRef
    DatabaseReference agencyRef;

    private UserAuthenticationListener listener = new UserAuthenticationListener();
    private AuthCallback authCallback;
    private DatabaseReference db;
    private Context context;

    public UserInfo(Context context) {
        this.context = context;
        DependencyInjector.applicationComponent().inject(this);
    }

    public void authUser(final AuthCallback authCallback) {
        System.out.println("herehererererer");
        this.authCallback = authCallback;

        for (String nodeName : users) {
            db = database.child(nodeName);
            db.addListenerForSingleValueEvent(listener);
        }
    }

    public void removeListeners() {
        db.removeEventListener(listener);
    }

    private void saveUser(UserRealm user) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(user);
        realm.commitTransaction();
        realm.close();
    }

    public User getUser() {
        UserRealm r = new UserRealm().getByPrimaryKey(Realm.getDefaultInstance(), userId);
        if(r == null) {
            return new User();
        }
        return r.toUser();
    }

    private void populateUser(String nodeName, DataSnapshot userNode) {

        int userType = getUserType(nodeName);

        String agencyAdmin = "";
        String systemAdmin = "";

        if(userNode.child("agencyAdmin").hasChildren()) {
            agencyAdmin = userNode.child("agencyAdmin").getChildren().iterator().next().getKey();
            PreferHelper.putString(AlertApplication.getContext(), Constants.AGENCY_ID, agencyAdmin);
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
        System.out.println("user = " + user);
        saveUser(user);
        if(authCallback != null) {
            authCallback.onUserAuthorized(user.toUser());
        }

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


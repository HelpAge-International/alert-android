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
import org.alertpreparedness.platform.alert.dagger.AlertRef;
import org.alertpreparedness.platform.alert.dagger.UserRef;
import org.alertpreparedness.platform.alert.interfaces.AuthCallback;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.DBListener;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by faizmohideen on 08/11/2017.
 */

public class UserInfo {
    private static CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static String[] users = {"administratorCountry", "countryDirector", "ert", "ertLeader", "partner"};
    private static DBListener dbListener = new DBListener();

    public DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    public static final String PREFS_USER = "prefs_user";

    DatabaseReference userRef;

    public void authUser(final AuthCallback authCallback) {
        for (String nodeName : users) {
            ValueEventListener valueEventListener;
            DatabaseReference db = database.child(PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS))
                    .child(nodeName);
            db.addListenerForSingleValueEvent(valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(PreferHelper.getString(AlertApplication.getContext(), Constants.UID)).exists()) {
                                Log.e("Tag", "TRUE"+nodeName);
                                DataSnapshot userNode = dataSnapshot.child(PreferHelper.getString(AlertApplication.getContext(), Constants.UID));
                                Log.e("Tag", "UID"+userNode);
                                populateUser(authCallback, nodeName, userNode);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
            dbListener.add(db, valueEventListener);

        }
    }

    private static void saveUser(Context context, User user) {
        String serializedUser = new Gson().toJson(user);
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREFS_USER, serializedUser)
                .apply();
        Log.e("USER", serializedUser);
    }

    public static User getUser(Context context) {
        String serializedUser = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREFS_USER, null);

        return new Gson().fromJson(serializedUser, User.class);
    }

    private void populateUser(AuthCallback callback, String nodeName, DataSnapshot userNode) {
        String userID = PreferHelper.getString(AlertApplication.getContext(), Constants.UID);
        int userType = getUserTypeString(nodeName);
        boolean isCountryDirector = false;
        String agencyAdmin = userNode.child("agencyAdmin").getChildren().iterator().next().getKey();
        String systemAdmin = userNode.child("systemAdmin").getChildren().iterator().next().getKey();
        String countryId = userNode.child("countryId").getValue(String.class);

        PreferHelper.putString(AlertApplication.getContext(), Constants.AGENCY_ID, agencyAdmin);
        PreferHelper.putString(AlertApplication.getContext(), Constants.COUNTRY_ID, countryId);
        PreferHelper.putString(AlertApplication.getContext(), Constants.SYSTEM_ID, systemAdmin);
        PreferHelper.putInt(AlertApplication.getContext(), Constants.USER_TYPE, userType);

       // Log.e("LINK", userRef.getRoot().toString());

        if(nodeName.equals("countryDirector")){
            isCountryDirector = true;
            User user = new User(userID, userType, agencyAdmin, countryId, systemAdmin, isCountryDirector);
            saveUser(callback.getContext(), user);
            callback.onUserAuthorized(user);
        }else{
            User user = new User(userID, userType, agencyAdmin, countryId, systemAdmin, isCountryDirector);
            saveUser(callback.getContext(), user);
            callback.onUserAuthorized(user);
        }

      //  System.out.println("LINK: "+ database.child(""));


    }


    private int getUserTypeString(String node) {
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

    public static void clearAll() {
        compositeDisposable.clear();
        dbListener.detatch();
    }

}


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
import org.alertpreparedness.platform.alert.interfaces.AuthCallback;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.risk_monitoring.service.NetworkService;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.DBListener;
import org.alertpreparedness.platform.alert.utils.PreferHelper;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by faizmohideen on 08/11/2017.
 */

public class UserInfo {
    private static CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static String[] users = {"hazard", "administratorCountry", "countryDirector", "ert", "ertLeader", "partner"};
    private static DBListener dbListener = new DBListener();
    private String country, network;

    public DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    public static final String PREFS_USER = "prefs_user";

    DatabaseReference userRef;

    public void authUser(final AuthCallback authCallback, Context context) {
        for (String nodeName : users) {

                DatabaseReference db = database.child(PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS)).child(nodeName);
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child(PreferHelper.getString(AlertApplication.getContext(), Constants.UID)).exists()) {
                            DataSnapshot userNode = dataSnapshot.child(PreferHelper.getString(AlertApplication.getContext(), Constants.UID));
                            populateUser(authCallback, nodeName, userNode, context);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
    }

    private void setHazardId(AuthCallback authCallback, Context context, String userID, int userType, String agencyAdmin, String countryId, String systemAdmin, boolean isCountryDirector ) {
        //TODO hazard ID

        country = UserInfo.getUser(context).countryID;
        System.out.println("CID: "+country);

        DatabaseReference db = database.child(PreferHelper.getString(AlertApplication.getContext(), Constants.APP_STATUS)).child("hazard");
        db.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Disposable NSDisposable = NetworkService.INSTANCE.mapNetworksForCountry(agencyAdmin, countryId).subscribe(
                        (Map<String, String> stringStringMap) -> {
                            for (String key : stringStringMap.keySet()) {
                                String networkID = stringStringMap.get(key);
                                String ids[] = {countryId, networkID};


                                for (String id: ids) {
                                    DataSnapshot hazardNode = dataSnapshot.child(id);
                                    Iterable<DataSnapshot> hazardChildren = hazardNode.getChildren();
                                    //ArrayList <User> users = new ArrayList<>();

                                    for(DataSnapshot ds: hazardChildren){
                                       // User user = new User(userID, userType, agencyAdmin, countryId, systemAdmin, networkID, ds, isCountryDirector);

                                        System.out.println("DataSnap: " + ds.getKey());
                                        //users.add(user);
                                    }
                                  //  ArrayList <String> hazardId = new ArrayList<>();
                                   // System.out.println("DataSnap: " + hazardId);
                                    // User user = new User(userID, userType, agencyAdmin, countryId, systemAdmin, networkID,  isCountryDirector);

                                    //saveUser(authCallback.getContext(), user);
                                    //authCallback.onUserAuthorized(user);
                                }
                                //TODO get all network id from networks
                            }
                        }
                );
                compositeDisposable.add(NSDisposable);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

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

    private void populateUser(AuthCallback callback, String nodeName, DataSnapshot userNode, Context context) {

        String userID = PreferHelper.getString(AlertApplication.getContext(), Constants.UID);
        int userType = getUserType(nodeName);
        boolean isCountryDirector = false;


        String agencyAdmin = userNode.child("agencyAdmin").getChildren().iterator().next().getKey();
        String systemAdmin = userNode.child("systemAdmin").getChildren().iterator().next().getKey();
        String countryId = userNode.child("countryId").getValue(String.class);


        PreferHelper.putString(AlertApplication.getContext(), Constants.AGENCY_ID, agencyAdmin);
        PreferHelper.putString(AlertApplication.getContext(), Constants.COUNTRY_ID, countryId);
        PreferHelper.putString(AlertApplication.getContext(), Constants.SYSTEM_ID, systemAdmin);
        PreferHelper.putInt(AlertApplication.getContext(), Constants.USER_TYPE, userType);


        if (nodeName.equals("countryDirector")) {
            isCountryDirector = true;
            User user = new User(userID, userType, agencyAdmin, countryId, systemAdmin, null, null, isCountryDirector);
            saveUser(callback.getContext(), user);
            callback.onUserAuthorized(user);
            setHazardId(callback, context, userID, userType, agencyAdmin, countryId, systemAdmin, isCountryDirector);
        }
        else {
            User user = new User(userID, userType, agencyAdmin, countryId, systemAdmin,null, null, isCountryDirector);
            saveUser(callback.getContext(), user);
            callback.onUserAuthorized(user);
            setHazardId(callback, context, userID, userType, agencyAdmin, countryId, systemAdmin, isCountryDirector);
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

    public static void clearAll() {
        compositeDisposable.clear();
        dbListener.detatch();
    }

}


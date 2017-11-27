package org.alertpreparedness.platform.alert.helper;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.alertpreparedness.platform.alert.login.activity.AuthCallback;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.risk_monitoring.service.NetworkService;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.DBListener;

import java.util.Locale;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by faizmohideen on 08/11/2017.
 */

public class UserInfo {
    public static String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public static DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    public static final String PREFS_USER = "prefs_user";
    private static CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static String[] users = {"administratorCountry", "countryDirector", "ert", "ertLeader", "partner"};
    private static DBListener dbListener = new DBListener();
    private static ValueEventListener valueEventListener;


    //Cross-check if the logged-in user ID matches the ID under different node.
    public static void authUser(final AuthCallback authCallback) {
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        for (String nodeName : users) {
            database.child(DataHandler.mAppStatus)
                    .child(nodeName)
                    .addListenerForSingleValueEvent(valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(userID).exists()) {
                                DataSnapshot userNode = dataSnapshot.child(userID);
                                populateUser(authCallback, nodeName, userNode);
                            } else {
                                //System.out.println("False");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
            dbListener.add(database, valueEventListener);

        }
    }

    public static void saveUser(Context context, User user) {
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

    private static void populateUser(AuthCallback callback, String nodeName, DataSnapshot userNode) {
        String userID = UserInfo.userID;
        int userType = getUserTypeString(nodeName);
        String agencyAdmin = userNode.child("agencyAdmin").getChildren().iterator().next().getKey();
        String systemAdmin = userNode.child("systemAdmin").getChildren().iterator().next().getKey();
        String countryId = userNode.child("countryId").getValue(String.class);

        Disposable NSDisposable = NetworkService.INSTANCE.mapNetworksForCountry(agencyAdmin, countryId).subscribe(
                (Map<String, String> stringStringMap) -> {
                    for (String key : stringStringMap.keySet()) {
                        //System.out.println("Agency "+ agencyAdmin);
                        String networkID = stringStringMap.get(key);

                        User user = new User(userID, userType, agencyAdmin, countryId, systemAdmin, networkID);
                        Toast.makeText(callback.getContext(),
                                String.format(Locale.getDefault(), "user: %s, type: %s, agency: %s, system: %s, country: %s, network: %s",
                                        userID, userType, agencyAdmin, systemAdmin, countryId, networkID),
                                Toast.LENGTH_LONG).show();

                        saveUser(callback.getContext(), user);
                        callback.onUserAuthorized(user);

                        //TODO get all network id from networks
                    }
                }
        );
        compositeDisposable.add(NSDisposable);
    }


    public static int getUserTypeString(String node) {
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


    public interface SimpleCallback {
        void callback(Object data);
    }

    public interface AdditionalCallback {
        void callback(Object data, String node);
    }

    public static void clearAll() {
        compositeDisposable.clear();
        dbListener.detatch();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        compositeDisposable.clear();
//        compositeDisposable.dispose();
//        dbListener.detatch();
//    }
}


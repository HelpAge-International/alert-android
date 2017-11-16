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

import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.Constants;

import java.util.Locale;

import timber.log.Timber;

/**
 * Created by faizmohideen on 08/11/2017.
 */

public class UserInfo {
    public static String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public static DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    public static final String PREFS_USER = "prefs_user";

    //Cross-check if the logged-in user ID matches the ID under different node.
    public static void getUserType(final Context context, final String nodeName){
        database.child("sand")
                .child(nodeName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Timber.d("uid: %s", userID);
                        //Callback for user type
                        //finishedCallback.callback(dataSnapshot.child(userID).exists(), nodeName);
                        if(dataSnapshot.child(userID).exists()){
                            DataSnapshot userNode = dataSnapshot.child(userID);
                            //System.out.println("DATA "+ dataSnapshot.child(userID));
                            populateUser(context, nodeName, userNode);
                        }else{
                            //System.out.println("False");
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
    }

    public static void saveUser(Context context, User user){
        String serializedUser = new Gson().toJson(user);
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREFS_USER, serializedUser)
        .apply();
        Log.e("USER", serializedUser);
    }

    public static User getUser(Context context){
        String serializedUser = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREFS_USER, null);

        return new Gson().fromJson(serializedUser, User.class);
    }

    private static void populateUser(Context context,String nodeName, DataSnapshot userNode) {
        String userID = UserInfo.userID;
        int userType = getUserTypeString(nodeName);
        String agencyAdmin = userNode.child("agencyAdmin").getChildren().iterator().next().getKey();
        String systemAdmin = userNode.child("systemAdmin").getChildren().iterator().next().getKey();
        String countryId = userNode.child("countryId").getValue(String.class);
        
        User user = new User(userID, userType, agencyAdmin, countryId, systemAdmin);
        Toast.makeText(context,
                String.format(Locale.getDefault(), "user: %s, type: %s, agency: %s, system: %s, country: %s",
                        userID, userType, agencyAdmin, systemAdmin, countryId),
                Toast.LENGTH_LONG).show();
        saveUser(context, user);
    }

    public static int getUserTypeString(String node){
        switch (node) {
            case "administratorCountry":
                return Constants.CountryAdmin;
            case "countryDirector":
                return Constants.CountryDirector;
            case "ert":
                return  Constants.Ert;
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

}


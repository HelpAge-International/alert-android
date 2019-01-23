package org.alertpreparedness.platform.v1.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import org.alertpreparedness.platform.v1.firebase.ActionModel;
import org.alertpreparedness.platform.v1.firebase.ClockSetting;
import org.alertpreparedness.platform.v1.firebase.FirebaseModel;
import org.alertpreparedness.platform.v1.helper.DateHelper;
import org.joda.time.DateTime;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtils {

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    public static boolean isDeviceOnline(Context context) {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static boolean isEmailValid(String email) {
        String expression = ".*@.*";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public static void hideSoftKeyboard(Context context, View view) {
        view.clearFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context
                .INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static int getStatusBarHeight(Activity context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    // Set the fragments for a page
    public static void setFragment(AppCompatActivity activity, @IdRes int containerId, Fragment fragment) {
        setFragment(activity, containerId, fragment, null);
    }

    public static void setFragment(AppCompatActivity activity, @IdRes int containerId, Fragment fragment, String tag) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        if (tag != null) {
            transaction.replace(containerId, fragment, tag);
        } else {
            transaction.replace(containerId, fragment);
        }
        transaction.commit();
    }

    public static void setTaskBarCol(Activity context, int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES
                .LOLLIPOP) {
            Window w = context.getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams
                    .FLAG_TRANSLUCENT_STATUS);
            //status bar height
            int statusBarHeight = getStatusBarHeight(context);

            View view = new View(context);
            view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                    .LayoutParams.WRAP_CONTENT));
            view.getLayoutParams().height = statusBarHeight;
            ((ViewGroup) w.getDecorView()).addView(view);
            view.setBackgroundColor(statusBarColor);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getWindow().setStatusBarColor(statusBarColor);
        }
    }

    //firebase call
    private static FirebaseDatabase sDatabase;

    public static FirebaseDatabase getDatabase() {
        if (sDatabase == null) {
            sDatabase = FirebaseDatabase.getInstance();
        }
        return sDatabase;
    }

    public static String getUserTypeString(int userType) {
        switch (userType) {
            case Constants.CountryAdmin:
                return "Country Admin";
            case Constants.CountryDirector:
                return "Country Director";
            case Constants.Ert:
                return "ERT";
            case Constants.ErtLeader:
                return "ERT Lead";
            default:
                return "Partner";
        }
    }

    //firebase storage call
    public static StorageReference getStorageReference() {
        return FirebaseStorage.getInstance().getReferenceFromUrl("gs://alert-190fa.appspot.com");
    }

    public static <T> T getValueFromDataSnapshot(DataSnapshot dataSnapshot, Class<T> clazz) {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        final Gson gson = gsonBuilder.setExclusionStrategies(new SnapshotExclusionStrat()).create();

        JsonReader reader = new JsonReader(new StringReader(gson.toJson(dataSnapshot.getValue()).trim()));
        reader.setLenient(true);
        return gson.fromJson(reader, clazz);
    }

    public static void sendNotification(Context context, String notificationTag, Notification notification) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("alert",
                        "Default Alert Notification Channel",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(notificationTag, new Random().nextInt(), notification);
        }
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Returns a {@link FirebaseModel} POJO instance from the data snapshot.
     * */
    public static <T extends FirebaseModel> T getFirebaseModelFromDataSnapshot(DataSnapshot dataSnapshot,
                                                                               Class<T> clazz) {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        final Gson gson = gsonBuilder.setExclusionStrategies(new SnapshotExclusionStrat()).create();

        JsonReader reader = new JsonReader(new StringReader(gson.toJson(dataSnapshot.getValue()).trim()));
        reader.setLenient(true);

        T mappedObject = gson.fromJson(reader, clazz);
        mappedObject.setId(dataSnapshot.getKey());
        mappedObject.setParentId(dataSnapshot.getRef().getParent().getKey());

        return mappedObject;
    }

    public static List<String> smartCombine(List<String> first, List<String> second) {
        first.addAll(second);
        return new ArrayList<>(new HashSet<>(first));
    }

    public static <T> Collection<T> combineDataSnapshotList(Object[] lists) {
        Collection<T> toReturn = new ArrayList<>();
        for (Object dataSnapshotList : lists) {
            toReturn.addAll((Collection<T>) dataSnapshotList);
        }
        return toReturn;
    }

    public static <K, V> Map<K, V> combinePairToMap(Object[] objects) {
        Map<K, V> map = new HashMap<>();
        for(Object pairObj : objects){
            Pair<K, V> pair = (Pair<K, V>)pairObj;
            map.put(pair.first, pair.second);
        }
        return map;
    }

    public static boolean isActionInProgress(ActionModel actionModel, ClockSetting clockSetting) {
        boolean res = false;
        Long timestamp = 0L;
        if(actionModel.hasCustomClockSettings()) {
            timestamp = (actionModel.getUpdatedAt() == null ? actionModel.getCreatedAt() : actionModel.getUpdatedAt());
        }
        else {
            timestamp = (actionModel.getUpdatedAt() == null ? actionModel.getCreatedAt() : actionModel.getUpdatedAt());
        }
        try {
            res = DateHelper.clockCalculation(clockSetting.getValue(), clockSetting.getDurationType()) + timestamp >= new DateTime().getMillis();
        }
        catch (Exception e) {}

        return res;
    }
}

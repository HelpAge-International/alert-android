package org.alertpreparedness.platform.alert.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.alertpreparedness.platform.alert.model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Fei on 15/06/2016.
 */
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
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public static void hideSoftKeyboard(Context context, View view) {
        view.clearFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
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
        }
        else {
            transaction.replace(containerId, fragment);
        }
        transaction.commit();
    }

    public static void setTaskBarCol(Activity context, int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Window w = context.getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
            int statusBarHeight = getStatusBarHeight(context);

            View view = new View(context);
            view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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

}

package org.alertpreparedness.platform.alert;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.alertpreparedness.platform.alert.utils.AppUtils;

import butterknife.ButterKnife;


public abstract class BaseActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    public ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;

    public abstract void initViews();

    public abstract void initToolbar();

    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState, int resourceId) {
//        checkAuth();
        super.onCreate(savedInstanceState);
        setContentView(resourceId);
        ButterKnife.bind(this);
        initToolbar();
        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAuth != null) {
            mAuth.removeAuthStateListener(this);
        }
    }

    protected void checkAuth() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(this);
    }

    public String getUid() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        }
        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            hideProgressDialog();
        }
    }

    public void setStatusBarCol(int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(statusBarColor);
        } else {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
            int statusBarHeight = AppUtils.getStatusBarHeight(this);

            View view = new View(this);
            view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.getLayoutParams().height = statusBarHeight;
            ((ViewGroup) w.getDecorView()).addView(view);
            view.setBackgroundColor(statusBarColor);
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

//    @Override
//    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//        // If Not authenticated again
//        if (firebaseAuth.getCurrentUser() == null) {
//            // User has logged out
//            PreferHelper.putString(getApplicationContext(), Constants.UID, "");
//            PreferHelper.putString(getApplicationContext(), Constants.USER_PASSWORD, "");
//            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(i);
//            finish();
//        }
//    }
}

package org.alertpreparedness.platform.v1.login.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.iid.FirebaseInstanceId;

import org.alertpreparedness.platform.v1.AlertApplication;
import org.alertpreparedness.platform.v1.R;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dashboard.activity.HomeScreen;
import org.alertpreparedness.platform.v1.helper.UserInfo;
import org.alertpreparedness.platform.v1.interfaces.AuthCallback;
import org.alertpreparedness.platform.v1.model.User;
import org.alertpreparedness.platform.v1.notifications.NotificationIdHandler;
import org.alertpreparedness.platform.v1.utils.AppUtils;
import org.alertpreparedness.platform.v1.utils.Constants;
import org.alertpreparedness.platform.v1.utils.PreferHelper;
import org.alertpreparedness.platform.v1.utils.SettingsFactory;
import org.alertpreparedness.platform.v1.utils.SnackbarHelper;


public class LoginScreen extends AppCompatActivity implements View.OnClickListener, OnFailureListener, OnCompleteListener<AuthResult>,AuthCallback {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    private EditText et_emailAddress;
    private EditText et_password;
    private Button btn_login;
    private TextView txt_forgotPasword;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private boolean isPasswordShowing = false;
    private boolean validPlayServices = true;

    UserInfo userInfo;
    private ImageView img_eye;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        DependencyInjector.initialize((AlertApplication) getApplication());

        userInfo = new UserInfo();

        userInfo.setActivity(this);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        et_emailAddress = (EditText) findViewById(R.id.email_address);
        et_password = (EditText) findViewById(R.id.password);
        et_emailAddress.setText("AUERTL1GAgM1@mailinator.com");
        et_password.setText("testtest1");

        img_eye = (ImageView) findViewById(R.id.imgEye);
        btn_login = (Button) findViewById(R.id.btnLogin);
        txt_forgotPasword = (TextView) findViewById(R.id.forgotPassword);

        btn_login.setOnClickListener(this);
        txt_forgotPasword.setOnClickListener(this);
        img_eye.setOnClickListener(v -> {
            if(isPasswordShowing) {
                et_password.setTransformationMethod(new PasswordTransformationMethod());
            }
            else {
                et_password.setTransformationMethod(null);
            }
            isPasswordShowing= !isPasswordShowing;
        });

        System.out.println("GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext()) = " + GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext()));

        if(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext()) != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED, this, 1).show();
            validPlayServices = false;
        }

    }

    @Override
    public void onClick(View view) {
        if (view == btn_login) {
            if(validPlayServices) {
                AppUtils.hideKeyboard(this);
                loginUser();
            }
            else {
                SnackbarHelper.show(this, getString(R.string.play_service_error));
            }
        }

        if (view == txt_forgotPasword) {
            switch (AlertApplication.CURRENT_STATUS) {

                case LIVE:
                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://platform.alertpreparedness.org/forgot-password")));
                    break;
                default:
                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://uat.portal.alertpreparedness.org/forgot-password")));
                    break;
            }
        }
    }

    private void loginUser() {

        String email = et_emailAddress.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            SnackbarHelper.show(this, getString(R.string.enter_email_error));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            SnackbarHelper.show(this, getString(R.string.enter_password_error));
            return;
        }

        progressDialog.setMessage("Logging you in...");
        progressDialog.show();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, this)
                .addOnFailureListener(this);
    }

    @Override
    public void onUserAuthorized(User user) {
        progressDialog.dismiss();
        SettingsFactory.tryMakeBaseSettings(user);
        startActivity(new Intent(this, HomeScreen.class));
        finish();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        e.printStackTrace();
        progressDialog.dismiss();
        if(e instanceof FirebaseAuthInvalidUserException) { //no user found
            SnackbarHelper.show(this, "The email address you entered is not associated with an ALERT account. Please try again.");
        }
        else if(e instanceof FirebaseAuthInvalidCredentialsException) {//incorrect password

            SnackbarHelper.show(this, "The password you entered is incorrect, please check and try again.");
        }
        else if (e instanceof FirebaseAuthException) {
            String error = ((FirebaseAuthException) e).getErrorCode();

            SnackbarHelper.show(this, e.getMessage());
        }

    }

    //login successful
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
            if (firebaseAuth.getCurrentUser()!=null) {
                DependencyInjector.initialize((AlertApplication) getApplication());
                DependencyInjector.initializeUserScope();
                PreferHelper.putString(this, Constants.UID, firebaseAuth.getCurrentUser().getUid());
                userInfo.authUser(this, PreferHelper.getString(this, Constants.UID));
                new NotificationIdHandler().registerDeviceId(firebaseAuth.getCurrentUser().getUid(), FirebaseInstanceId.getInstance().getToken());
            }
        }
        else {
            SnackbarHelper.show(this, "The email address you entered is not associated with an ALERT account. Please try again.");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        userInfo.removeListeners();
    }
}


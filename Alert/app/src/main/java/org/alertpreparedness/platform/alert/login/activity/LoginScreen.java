package org.alertpreparedness.platform.alert.login.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import org.alertpreparedness.platform.alert.AlertApplication;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dashboard.activity.HomeScreen;
import org.alertpreparedness.platform.alert.helper.RightDrawableOnTouchListener;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.interfaces.AuthCallback;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.settings.SettingsFragment;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.PreferHelper;
import org.alertpreparedness.platform.alert.utils.SettingsFactory;
import org.alertpreparedness.platform.alert.utils.SnackbarHelper;

import javax.inject.Inject;

import timber.log.Timber;


public class LoginScreen extends AppCompatActivity implements View.OnClickListener, OnFailureListener, OnCompleteListener<AuthResult>,AuthCallback {

    private EditText et_emailAddress;
    private EditText et_password;
    private Button btn_login;
    private TextView txt_forgotPasword;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private boolean isPasswordShowing = false;

    @Inject
    UserInfo userInfo;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        DependencyInjector.applicationComponent().inject(this);

        userInfo.setActivity(this);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        et_emailAddress = (EditText) findViewById(R.id.email_address);
        et_password = (EditText) findViewById(R.id.password);
        btn_login = (Button) findViewById(R.id.btnLogin);
        txt_forgotPasword = (TextView) findViewById(R.id.forgotPassword);

        btn_login.setOnClickListener(this);
        txt_forgotPasword.setOnClickListener(this);
        et_password.setOnTouchListener(new RightDrawableOnTouchListener(et_password) {
            @Override
            public boolean onDrawableTouch(final MotionEvent event) {
                if(isPasswordShowing) {
                    et_password.setTransformationMethod(new PasswordTransformationMethod());
                }
                else {
                    et_password.setTransformationMethod(null);
                }
                isPasswordShowing= !isPasswordShowing;
                return true;
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view == btn_login) {
            loginUser();
        }

        if (view == txt_forgotPasword) {
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://platform.alertpreparedness.org/forgot-password")));
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

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, this)
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

        progressDialog.dismiss();
        if(e instanceof FirebaseAuthInvalidUserException) { //no user found
            SnackbarHelper.show(this, getString(R.string.email_not_associated_with_account));
        }
        else if(e instanceof FirebaseAuthInvalidCredentialsException) {//incorrect password

            SnackbarHelper.show(this, getString(R.string.password_incorrect));
        }
        else if (e instanceof FirebaseAuthException) {
            String error = ((FirebaseAuthException) e).getErrorCode();

            SnackbarHelper.show(this, e.getMessage());
        }

    }

    //login successful
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        progressDialog.dismiss();
        if (task.isSuccessful()) {
            System.out.println("firebaseAuth.getCurrentUser() = " + firebaseAuth.getCurrentUser());
            if (firebaseAuth.getCurrentUser()!=null) {
                PreferHelper.putString(this, Constants.UID, firebaseAuth.getCurrentUser().getUid());
                userInfo.authUser(this, PreferHelper.getString(this, Constants.UID));
            }
        }
        else {
            SnackbarHelper.show(this, getString(R.string.email_not_associated_with_account));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        userInfo.removeListeners();
    }
}


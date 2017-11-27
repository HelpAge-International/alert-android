package org.alertpreparedness.platform.alert.login.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dashboard.activity.HomeScreen;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.model.User;


public class LoginScreen extends AppCompatActivity implements View.OnClickListener, AuthCallback {

    private EditText et_emailAddress;
    private EditText et_password;
    private Button btn_login;
    private TextView txt_forgotPasword;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private UserInfo userInfo = new UserInfo();
    private final static String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        et_emailAddress = (EditText) findViewById(R.id.email_address);
        et_password = (EditText) findViewById(R.id.password);
        btn_login = (Button) findViewById(R.id.btnLogin);
        txt_forgotPasword = (TextView) findViewById(R.id.forgotPassword);

        btn_login.setOnClickListener(this);
        txt_forgotPasword.setOnClickListener(this);

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
            Toast.makeText(this, "Please enter your email!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Logging you in...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
//                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            userInfo.authUser(LoginScreen.this);
                        }

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed | " + task.getException().getMessage());
                            Toast.makeText(LoginScreen.this, "The password you entered is incorrect. Please check and try again!" + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onUserAuthorized(User user) {
        progressDialog.dismiss();
        startActivity(new Intent(getApplicationContext(), HomeScreen.class));
        finish();
    }

    @Override
    public Context getContext() {
        return this;
    }
}


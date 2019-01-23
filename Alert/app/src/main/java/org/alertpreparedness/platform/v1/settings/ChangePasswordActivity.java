package org.alertpreparedness.platform.v1.settings;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.alertpreparedness.platform.v1.R;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.utils.SnackbarHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Tj on 27/12/2017.
 */

public class ChangePasswordActivity extends AppCompatActivity implements OnCompleteListener<Void>, OnFailureListener, TextWatcher {

    public final static String PASSWORD_KEY = "email";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.etPassword)
    TextInputEditText mPassword;

    @BindView(R.id.etNewPassword)
    TextInputEditText mNewPassword;

    @BindView(R.id.etReenterPassword)
    TextInputEditText mReenterPAssword;

    @BindView(R.id.reenterBae)
    TextInputLayout reenterBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        ButterKnife.bind(this);
        DependencyInjector.userScopeComponent().inject(this);

        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();
    }

    private void initViews() {
        mReenterPAssword.addTextChangedListener(this);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        return true;
    }

    @OnClick(R.id.btnSave)
    void onSaveClick(View v) {
        String password = mPassword.getText().toString();

        System.out.println("mReenterPAssword.getText() = " + mReenterPAssword.getText().toString());
        System.out.println("mNewPassword.getText() = " + mNewPassword.getText().toString());
        System.out.println("passwordequals = " + mNewPassword.getText().toString().equals(mReenterPAssword.getText().toString()));

        if(password.length() == 0) {
            SnackbarHelper.show(this, getString(R.string.enter_password_error));
        }
        else if(mNewPassword.getText().length() == 0) {
            SnackbarHelper.show(this, getString(R.string.enter_new_password_error));
        }
        else if(mReenterPAssword.getText().length() == 0) {
            SnackbarHelper.show(this, getString(R.string.reenter_new_password_error));
        }
        else if(!mReenterPAssword.getText().toString().equals(mNewPassword.getText().toString())) {
            SnackbarHelper.show(this, getString(R.string.passwords_dont_match_error));
        }
        else {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            assert user != null;
            assert user.getEmail() != null;
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
            user.reauthenticate(credential).addOnCompleteListener(this).addOnFailureListener(this);


        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }


    @Override
    public void onComplete(@NonNull Task<Void> task) {
        Intent i = new Intent();
        i.putExtra(PASSWORD_KEY, mNewPassword.getText().toString());
        setResult(RESULT_OK, i);
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        SnackbarHelper.show(this, getString(R.string.curreny_pas_incorrect_error));
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(mNewPassword.getText() != null && !mNewPassword.getText().toString().equals(mReenterPAssword.getText().toString())) {
            reenterBase.setError(getString(R.string.passwords_dont_match_temp_error));
        }
        else {
            reenterBase.setError("");
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}

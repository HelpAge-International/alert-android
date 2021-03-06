package org.alertpreparedness.platform.v1.settings;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import org.alertpreparedness.platform.R;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.utils.AppUtils;
import org.alertpreparedness.platform.v1.utils.SnackbarHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Tj on 27/12/2017.
 */

public class ChangeEmailActivity extends AppCompatActivity {

    public final static String EMAIL_KEY = "email";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.etEmail)
    TextInputEditText mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        ButterKnife.bind(this);
        DependencyInjector.userScopeComponent().inject(this);

        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String email = getIntent().getStringExtra(EMAIL_KEY);
        mEmail.setText(email);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        return true;
    }

    @OnClick(R.id.btnSave)
    void onSaveClick(View v) {
        String email = mEmail.getText().toString();
        System.out.println("email = " + email);
        if(!AppUtils.isEmailValid(email)) {
            SnackbarHelper.show(this, getString(R.string.enter_valid_emial_error));
        }
        else if(email.length() == 0) {
            SnackbarHelper.show(this, getString(R.string.enter_email_error));
        }
        else {
            Intent i = new Intent();
            i.putExtra(EMAIL_KEY, email);
            setResult(RESULT_OK, i);
            finish();
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }


}

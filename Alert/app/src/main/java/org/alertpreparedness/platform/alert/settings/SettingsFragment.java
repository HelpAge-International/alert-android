package org.alertpreparedness.platform.alert.settings;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.alertpreparedness.platform.alert.MainDrawer;
import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.UserEmail;
import org.alertpreparedness.platform.alert.dagger.annotation.UserRef;
import org.alertpreparedness.platform.alert.helper.UserInfo;
import org.alertpreparedness.platform.alert.login.activity.LoginScreen;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.notifications.NotificationIdHandler;
import org.alertpreparedness.platform.alert.responseplan.ResponsePlansAdapter;
import org.alertpreparedness.platform.alert.utils.PreferHelper;
import org.alertpreparedness.platform.alert.utils.SnackbarHelper;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static org.alertpreparedness.platform.alert.MainDrawer.TAG;

/**
 * Created by Tj on 27/12/2017.
 */

public class SettingsFragment extends Fragment implements ValueEventListener {

    private static final int CHANGE_EMAIL = 9001;
    private static final int CHANGE_PASSWORD = 9002;

    @BindView(R.id.btnChangeEmail)
    Button mChangeEmail;

    @Inject @UserRef @Nullable
    DatabaseReference userRef;

    @Inject
    public User user;

    @Inject
    public NotificationIdHandler notificationIdHandler;


    private String email;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        ButterKnife.bind(this, v);
        DependencyInjector.applicationComponent().inject(this);

        initViews();

        ((MainDrawer)getActivity()).toggleActionBarWithTitle(MainDrawer.ActionBarState.NORMAL, R.string.nav_settings);

        userRef.addValueEventListener(this);

        return v;
    }

    private void initViews() {


    }

    @OnClick(R.id.btnLogout)
    void onLogoutClick(View v) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Logout")
                .setMessage("You will be unable to log back into the app unless you have internet connection. Are you sure you want to log out?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    if(FirebaseInstanceId.getInstance().getToken() != null) {
                        notificationIdHandler.deregisterDeviceId(user.getUserID(), FirebaseInstanceId.getInstance().getToken(), new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError == null){
                                    logout();
                                }
                                else{
                                    SnackbarHelper.show(getActivity(), getString(R.string.error_logging_out));
                                }
                            }
                        });
                    }
                    else {
                        logout();
                    }
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    // do nothing
                })
                .show();

    }

    private void logout() {
        PreferHelper.getInstance(getContext()).edit().remove(UserInfo.PREFS_USER).apply();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getContext(), LoginScreen.class));
        getActivity().finish();
    }

    @OnClick(R.id.btnChangeEmail)
    void onChangeEmailClick(View v) {
        Intent intent = new Intent(getContext(), ChangeEmailActivity.class);
        intent.putExtra(ChangeEmailActivity.EMAIL_KEY, email);
        startActivityForResult(intent, CHANGE_EMAIL);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    @OnClick(R.id.btnChangePassword)
    void onChangePasswordClick(View v) {
        Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
        startActivityForResult(intent, CHANGE_PASSWORD);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        email = (String)dataSnapshot.child("email").getValue();
        mChangeEmail.setText(email);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHANGE_EMAIL && resultCode == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            user.updateEmail(data.getStringExtra(ChangeEmailActivity.EMAIL_KEY))
                    .addOnCompleteListener(new EmailChangeComplete(data.getStringExtra(ChangeEmailActivity.EMAIL_KEY)))
                    .addOnFailureListener(new EmailChangeFailure());
        }

        if(requestCode == CHANGE_PASSWORD && resultCode == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            user.updatePassword(data.getStringExtra(ChangePasswordActivity.PASSWORD_KEY))
                    .addOnFailureListener(new PasswordChangeFailure());
        }
    }

    private class EmailChangeComplete implements OnCompleteListener<Void> {

        private String email;

        public EmailChangeComplete(String email) {

            this.email = email;
        }

        @Override
        public void onComplete(@NonNull Task<Void> task) {
            userRef.child("email").setValue(email);
        }
    }

    private class EmailChangeFailure implements OnFailureListener {


        @Override
        public void onFailure(@NonNull Exception e) {
            SnackbarHelper.show(getActivity(), getString(R.string.couldnt_update_email_error));
        }
    }

    private class PasswordChangeFailure implements OnFailureListener {
        @Override
        public void onFailure(@NonNull Exception e) {
            SnackbarHelper.show(getActivity(), getString(R.string.couldnt_update_password_error));

        }
    }
}

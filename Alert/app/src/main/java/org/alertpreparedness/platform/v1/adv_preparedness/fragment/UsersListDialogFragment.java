package org.alertpreparedness.platform.v1.adv_preparedness.fragment;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import javax.inject.Inject;
import org.alertpreparedness.platform.v1.R;
import org.alertpreparedness.platform.v1.adv_preparedness.adapter.UserListAdapter;
import org.alertpreparedness.platform.v1.adv_preparedness.model.UserModel;
import org.alertpreparedness.platform.v1.dagger.DependencyInjector;
import org.alertpreparedness.platform.v1.dagger.annotation.StaffRef;
import org.alertpreparedness.platform.v1.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.v1.model.User;

/**
 * Created by faizmohideen on 09/01/2018.
 */

@Deprecated
public class UsersListDialogFragment extends DialogFragment
        implements UserListAdapter.ItemSelectedListener, ValueEventListener {

    private Unbinder unbinder;
    private UserListAdapter mAdapter;
    private ArrayList<UserModel> userList;
    private int mPosition = 0;

    @BindView(R.id.rvUsersList)
    RecyclerView mList;

    @Inject
    @UserPublicRef
    DatabaseReference dbUserPublicRef;

    @Inject
    @StaffRef
    DatabaseReference staffRef;

    @Inject
    User user;

    private ItemSelectedListener listener;
    private UserModel mUser;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_users_list, null);
        unbinder = ButterKnife.bind(this, v);
        DependencyInjector.userScopeComponent().inject(this);

        mAdapter = new UserListAdapter(getContext(), dbUserPublicRef, this);
        mList.setAdapter(mAdapter);
        mList.setLayoutManager(new LinearLayoutManager(getActivity()));

        dbUserPublicRef.child(user.countryID).addValueEventListener(new UserListener());

        staffRef.addValueEventListener(this);

        builder.setView(v)
                .setPositiveButton(R.string.save, (dialog, id) -> {
                    listener.onItemSelected(mUser);
                })
                .setNegativeButton(R.string.close, (dialog, id) -> {
                });

        return builder.create();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            dbUserPublicRef.child(child.getKey()).addValueEventListener(new UserListener());
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onActionItemSelected(UserModel user) {
        this.mUser = user;
    }

    public void setListener(ItemSelectedListener listener) {
        this.listener = listener;
    }

    class UserListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            System.out.println("uselistdataSnapshot = " + dataSnapshot.getRef());
            System.out.println("uselistdataSnapshot = " + dataSnapshot);
            String firstname = (String) dataSnapshot.child("firstName").getValue();
            String lastname = (String) dataSnapshot.child("lastName").getValue();
            String fullname = String.format("%s %s", firstname, lastname);

            mAdapter.addUsers(dataSnapshot.getKey(), new UserModel(dataSnapshot.getKey(), fullname));
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    public interface ItemSelectedListener {

        void onItemSelected(@Nullable UserModel model);
    }
}
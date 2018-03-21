package org.alertpreparedness.platform.alert.min_preparedness.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseNoteRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserRef;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.AddNotesAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.model.Note;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.PermissionsHelper;
import org.alertpreparedness.platform.alert.utils.SnackbarHelper;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddNotesActivity extends AppCompatActivity implements AddNotesAdapter.ItemSelectedListener, ValueEventListener, View.OnClickListener {

    public static final String ACTION_ID = "ACTION_KEY";
    public static final String PARENT_ACTION_ID = "PARENT_ACTION_ID";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.rvIndicatorLog)
    RecyclerView recyclerView;

    @BindView(R.id.etIndicatorLog)
    EditText etNotes;

    @BindView(R.id.ivIndicatorLog)
    ImageView submitNote;

    @Inject
    @ActionRef
    DatabaseReference dbActionRef;

    @Inject
    @BaseNoteRef
    DatabaseReference dbNoteRef;

    @Inject
    @UserPublicRef
    DatabaseReference dbUserPublicRef;

    @Inject
    @UserRef
    DatabaseReference dbUserRef;

    @Inject
    User user;

    AddNotesAdapter addNotesAdapter;
    private String actionKey;
    private String actionParentKey;

    @Inject
    PermissionsHelper permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);

        ButterKnife.bind(this);
        DependencyInjector.userScopeComponent().inject(this);
        submitNote.setOnClickListener(this);

        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        actionKey = getIntent().getStringExtra(ACTION_ID);
        actionParentKey = getIntent().getStringExtra(PARENT_ACTION_ID);

        initData();
    }

    private void initData() {

        getData();
        initView();

    }

    private void getData() {
        dbNoteRef.child(actionParentKey).child(actionKey).addValueEventListener(this);
    }

    private void initView() {
        addNotesAdapter = new AddNotesAdapter(getApplicationContext(), dbNoteRef.child(actionParentKey).child(actionKey), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(addNotesAdapter);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot getChild : dataSnapshot.getChildren()) {
            String name = (String) getChild.child("uploadBy").getValue();
            String content = (String) getChild.child("content").getValue();
            Long time = (Long) getChild.child("time").getValue();
            setDataToAdapter(name, content, time, getChild.getKey());
        }
    }

    public void setDataToAdapter(String name, String content, Long time, String key) {
        dbUserPublicRef.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String firstname = (String) dataSnapshot.child("firstName").getValue();
                String lastname = (String) dataSnapshot.child("lastName").getValue();
                String fullname = String.format("%s %s", firstname, lastname);
                addNotesAdapter.addInProgressItem(key, new Note(
                        content,
                        time,
                        fullname
                ));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public void saveNote(String texts) {

        if(permissions.checkCreateNote()) {
            SnackbarHelper.show(this, getString(R.string.permission_note_create_error));
        }
        else if(!TextUtils.isEmpty(texts)) {
            String id = dbNoteRef.child(actionParentKey).child(actionKey).push().getKey();
            String userID = user.getUserID();
            Long millis = System.currentTimeMillis();

            Note notes = new Note(texts, millis, userID);
            dbNoteRef.child(actionParentKey).child(actionKey).child(id).setValue(notes);

        }
        else {
            SnackbarHelper.show(this, getString(R.string.txt_note_not_empty));
        }
    }

    @Override
    public void onClick(View view) {
        if(view == submitNote){
            String texts = etNotes.getText().toString().trim();
            saveNote(texts);
            etNotes.setText("");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onNoteItemSelected(int pos) {

    }

    @Override
    public void onNewITemAdded() {
        recyclerView.smoothScrollToPosition(addNotesAdapter.getItemCount()-1);
    }
}

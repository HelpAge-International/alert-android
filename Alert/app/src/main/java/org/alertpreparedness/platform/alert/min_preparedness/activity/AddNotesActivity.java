package org.alertpreparedness.platform.alert.min_preparedness.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.NoteRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserPublicRef;
import org.alertpreparedness.platform.alert.dagger.annotation.UserRef;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.AddNotesAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.model.Notes;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.PreferHelper;
import org.alertpreparedness.platform.alert.utils.SimpleAdapter;
import org.alertpreparedness.platform.alert.utils.SnackbarHelper;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddNotesActivity extends AppCompatActivity implements AddNotesAdapter.ItemSelectedListener, ValueEventListener, View.OnClickListener {

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
    @NoteRef
    DatabaseReference dbNoteRef;

    @Inject
    @UserPublicRef
    DatabaseReference dbUserPublicRef;

    @Inject
    @UserRef
    DatabaseReference dbUserRef;

    AddNotesAdapter addNotesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);

        ButterKnife.bind(this);
        DependencyInjector.applicationComponent().inject(this);
        submitNote.setOnClickListener(this);

        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initData();
    }

    private void initData() {
        dbActionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot getChild : dataSnapshot.getChildren()) {
                    getData(getChild.getKey());
                    initView(getChild.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("databaseError = " + databaseError);
            }
        });
    }

    private void getData(String key) {
        dbNoteRef.child(key).addListenerForSingleValueEvent(this);
    }

    protected AddNotesAdapter getmAdapter(String key) {
        return new AddNotesAdapter(getApplicationContext(), dbNoteRef.child(key), this);
    }

    private void initView(String key) {
        addNotesAdapter = getmAdapter(key);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
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
                addNotesAdapter.addInProgressItem(key, new Notes(
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

        if(!TextUtils.isEmpty(texts)) {
            Intent intent = getIntent();
            String key = intent.getStringExtra("ACTION_KEY");
            String id = dbNoteRef.child(key).push().getKey();
            String userID = PreferHelper.getString(getApplicationContext(), Constants.AGENCY_ID);
            Long millis = System.currentTimeMillis();

            Notes notes = new Notes(texts, millis, userID);
            dbNoteRef.child(key).child(id).setValue(notes);

        }else {
            SnackbarHelper.show(this, getString(R.string.txt_note_not_empty));
        }
    }

    @Override
    public void onClick(View view) {
        if(view == submitNote){
            String texts = etNotes.getText().toString().trim();
            saveNote(texts);
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
}

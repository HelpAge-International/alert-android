package org.alertpreparedness.platform.alert.min_preparedness.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.AgencyRef;
import org.alertpreparedness.platform.alert.dagger.annotation.NoteRef;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.AddNotesAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.model.Notes;
import org.alertpreparedness.platform.alert.utils.SimpleAdapter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddNotesActivity extends AppCompatActivity implements AddNotesAdapter.ItemSelectedListener, ValueEventListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.rvIndicatorLog)
    RecyclerView recyclerView;

    @Inject
    @ActionRef
    DatabaseReference dbActionRef;

    @Inject
    @NoteRef
    DatabaseReference dbNoteRef;

    AddNotesAdapter addNotesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);

        ButterKnife.bind(this);
        DependencyInjector.applicationComponent().inject(this);

        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initData();
        dbNoteRef.child("-KxIyUok6yZwYYL3jj9M").addListenerForSingleValueEvent( this);

        initView();

    }

    private void initData() {

        dbActionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot getChild : dataSnapshot.getChildren()) {
                    //System.out.println("dataSnapshot.getKey() = " + getChild.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("databaseError = " + databaseError);
            }
        });
    }

    protected AddNotesAdapter getmAdapter() {
        return new AddNotesAdapter(getApplicationContext(), dbNoteRef,  this);
    }


    private void initView() {
        addNotesAdapter = getmAdapter();
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(addNotesAdapter);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        System.out.println("dataSnapshot = " + dataSnapshot);
        for (DataSnapshot getChild : dataSnapshot.getChildren()) {
            String name = (String) getChild.child("uploadBy").getValue();
            String content = (String) getChild.child("content").getValue();
            Long time = (Long) getChild.child("time").getValue();

            System.out.println("time = " + time);
            System.out.println("content = " + content);
            System.out.println("name = " + name);

            addNotesAdapter.addInProgressItem(getChild.getKey(), new Notes(
                    name,
                    time,
                    content
            ));

        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }


    @Override
    public void onNoteItemSelected(int pos) {

    }
}

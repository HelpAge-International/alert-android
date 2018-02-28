package org.alertpreparedness.platform.alert.min_preparedness.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseDocumentRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseStorageRef;
import org.alertpreparedness.platform.alert.dagger.annotation.DocumentRef;
import org.alertpreparedness.platform.alert.firebase.DocumentModel;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.AppUtils;
import org.alertpreparedness.platform.alert.utils.DownloadFileFromURL;
import org.alertpreparedness.platform.alert.utils.SnackbarHelper;

import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * Created by Tj on 21/02/2018.
 */

public class ViewAttachmentsActivity extends AppCompatActivity implements ViewAttachmentAdapter.AttachementSelectListener {

    public static final String ACTION_ID = "ACTION_KEY";
    public static final String PARENT_ACTION_ID = "PARENT_ACTION_ID";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.rvAttachmentsList)
    RecyclerView attachmentList;

    @Inject
    @BaseStorageRef
    StorageReference mStorageRef;

    @Inject
    @BaseDocumentRef
    DatabaseReference dbDocRef;

    @Inject
    @BaseActionRef
    DatabaseReference dbActionBaseRef;

    @Inject
    User user;

    private String actionKey;
    private String actionParentKey;
    private ViewAttachmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attachments);

        ButterKnife.bind(this);
        DependencyInjector.applicationComponent().inject(this);

        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initData();
        initViews();
    }

    private void initData() {

        actionKey = getIntent().getStringExtra(ACTION_ID);
        actionParentKey = getIntent().getStringExtra(PARENT_ACTION_ID);
        dbActionBaseRef.child(actionParentKey).child(actionKey).child("documents").addChildEventListener(new FetchDocumentKeys());

    }

    private void initViews() {
        adapter = new ViewAttachmentAdapter(this, this);
        attachmentList.setAdapter(adapter);
        attachmentList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onAttachmentSelected(DocumentModel document) {
        System.out.println("document.getUploadedBy() = " + document.getUploadedBy());
        System.out.println("user.getUserID() = " + user.getUserID());
        if(user.getUserID().equals(document.getUploadedBy())) {
            SheetMenu.with(this).setMenu(R.menu.menu_attachment).setClick(menuItem -> {
                if (AppUtils.isDeviceOnline(this)) {
                    switch (menuItem.getItemId()) {
                        case R.id.delete:
                            dbActionBaseRef.child(actionParentKey).child(actionKey).child("documents").child(document.getKey()).removeValue();
                            dbDocRef.child(actionParentKey).child(document.getKey()).removeValue();
                            break;
                        case R.id.download:
                            new DownloadFileFromURL(this, document.getFileName()).execute(document.getFilePath());
                            break;
                    }
                } else {
                    SnackbarHelper.show(this, getString(R.string.online_error));
                }
                return false;
            }).show();
        }
        else {
            SheetMenu.with(this).setMenu(R.menu.menu_attachment_read_only).setClick(menuItem -> {
                if (AppUtils.isDeviceOnline(this)) {
                    switch (menuItem.getItemId()) {
                        case R.id.download:
                            new DownloadFileFromURL(this, document.getFileName()).execute(document.getFilePath());
                            break;
                    }
                } else {
                    SnackbarHelper.show(this, getString(R.string.online_error));
                }
                return false;
            }).show();
        }
    }

    private class FetchDocumentKeys implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            dbDocRef.child(actionParentKey).child(dataSnapshot.getKey()).addValueEventListener(new FetchDocumentListener());
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            dbDocRef.child(actionParentKey).child(dataSnapshot.getKey()).addValueEventListener(new FetchDocumentListener());
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            adapter.removeItem(dataSnapshot.getKey());
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private class FetchDocumentListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            DocumentModel model = dataSnapshot.getValue(DocumentModel.class);

            if(model != null) {
                model.setKey(dataSnapshot.getKey());
                model.setRef(dataSnapshot.getRef());
                adapter.addItem(model);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}
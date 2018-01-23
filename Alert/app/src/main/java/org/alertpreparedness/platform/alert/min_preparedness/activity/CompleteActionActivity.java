package org.alertpreparedness.platform.alert.min_preparedness.activity;

import android.support.v4.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseStorageRef;
import org.alertpreparedness.platform.alert.dagger.annotation.DocumentRef;
import org.alertpreparedness.platform.alert.dagger.annotation.NoteRef;
import org.alertpreparedness.platform.alert.dashboard.activity.HomeScreen;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertAdapter;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertFieldsAdapter;
import org.alertpreparedness.platform.alert.dashboard.model.Tasks;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.AttachmentAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.fragment.MinPreparednessFragment;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.min_preparedness.model.DataModel;
import org.alertpreparedness.platform.alert.min_preparedness.model.FileInfo;
import org.alertpreparedness.platform.alert.min_preparedness.model.Notes;
import org.alertpreparedness.platform.alert.model.User;
import org.alertpreparedness.platform.alert.utils.Constants;
import org.alertpreparedness.platform.alert.utils.PreferHelper;
import org.alertpreparedness.platform.alert.utils.SimpleAdapter;
import org.alertpreparedness.platform.alert.utils.SnackbarHelper;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.whalemare.sheetmenu.SheetMenu;

public class CompleteActionActivity extends AppCompatActivity implements SimpleAdapter.RemoveListener, View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.etCompletionNote)
    EditText editTextNote;

    @BindView(R.id.rvCompletionAttachments)
    RecyclerView recyclerView;

    @BindView(R.id.txtAddAttachments)
    TextView addAttachments;

    @Inject
    @BaseStorageRef
    StorageReference mStorageRef;

    @Inject
    @NoteRef
    DatabaseReference dbNoteRef;

    @Inject
    @ActionRef
    DatabaseReference dbActionRef;

    @Inject
    @DocumentRef
    DatabaseReference dbDocRef;

    @Inject
    User user;

    ArrayList<String> imgList = new ArrayList<>();
    ArrayList<String> pathList = new ArrayList<>();
    private static final long KB = 1024;

    SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_action);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ButterKnife.bind(this);
        DependencyInjector.applicationComponent().inject(this);

        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addAttachments.setOnClickListener(this);

        initView();

    }

    private void initView() {
        simpleAdapter = new SimpleAdapter(0, imgList, this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(simpleAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_confirm, menu);
        return true;
    }

    @Override
    public void onItemRemove(int positionInParent, int position) {
        imgList.remove(position);
        simpleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        if (view == addAttachments) {
            bottomSheet();
        }
    }

    public void bottomSheet() {
        SheetMenu.with(this).setTitle("Add attachment").setMenu(R.menu.menu_add_attachment).setClick(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.take_photo:
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 0);
                        break;
                    case R.id.take_video:
                        //TODO allow video files
                        break;
                    case R.id.select_file:
                        //TODO allow select files
                        showFileChooser();
                        break;
                }
                return false;
            }
        }).show();
    }

    private void showFileChooser() {
//        Intent intent = new Intent();
//        intent.setType("")


    }

    private void confirmActionComplete() {
        String notes = editTextNote.getText().toString().trim();

        if (TextUtils.isEmpty(notes)) {
            SnackbarHelper.show(this, getString(R.string.txt_err_complete_action_note));
            return;
        }

//        if (imgList.size() == 0) {
//            SnackbarHelper.show(this, getString(R.string.txt_err_add_attachments));
//        }

        saveData(notes);
    }

    private void saveData(String texts) {
        Intent intent = getIntent();
        String key = intent.getStringExtra("ACTION_KEY");

        for (int i = 0; i < pathList.size(); i++) {

            DatabaseReference ref = dbActionRef.child(key).child("documents").push();

            ref.setValue(true);
            saveNote(texts, key);

            int finalI = i;
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String documentID = dataSnapshot.getKey();
                    System.out.println("user.getUserID() = " + user.getUserID());
                    System.out.println("documentID = " + documentID);


                    StorageReference riversRef = mStorageRef.child("documents/" + user.getCountryID() + "/" + documentID + "/" + imgList.get(finalI));
                    System.out.println("Uri.parse(pathList.get(i)) = " + Uri.parse(pathList.get(finalI)));


                    riversRef.putFile(Uri.parse("file://" + pathList.get(finalI)))
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(getApplicationContext(), "Action completed successfully", Toast.LENGTH_LONG).show();
                                    editTextNote.setText("");
                                    imgList.remove(finalI);
                                    simpleAdapter.notifyDataSetChanged();

                                    String title = taskSnapshot.getMetadata().getName();
                                    String downloadUri = taskSnapshot.getMetadata().getDownloadUrl().toString();
                                    Long size = taskSnapshot.getMetadata().getSizeBytes();
                                    double sizeInKb = size / KB;
                                    Long time = System.currentTimeMillis();

                                    FileInfo info = new FileInfo(title, downloadUri, Long.valueOf(0), sizeInKb, Long.valueOf(0), time, title, user.getUserID());
                                    dbDocRef.child(documentID).setValue(info);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    System.out.println("exception = " + exception);
                                }
                            });
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void saveNote(String texts, String key) {
        if (!TextUtils.isEmpty(texts)) {
            String id = dbNoteRef.child(key).push().getKey();
            String userID = PreferHelper.getString(getApplicationContext(), Constants.AGENCY_ID);
            Long millis = System.currentTimeMillis();

            Notes notes = new Notes(texts, millis, userID);
            dbActionRef.child(key).child("isComplete").setValue(true);
            dbActionRef.child(key).child("isCompleteAt").setValue(millis);
            dbNoteRef.child(key).child(id).setValue(notes);

            Intent intent = new Intent(CompleteActionActivity.this, HomeScreen.class);
            startActivity(intent);
        } else {
            SnackbarHelper.show(this, getString(R.string.txt_note_not_empty));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap photo = (Bitmap) data.getExtras().get("data");

        Uri tempUri = getImageUri(getApplicationContext(), photo);

        System.out.println("tempUri = " + tempUri);

        Action action = new Action(tempUri);

        File finalFile = new File(getRealPathFromURI(action.getPath()));

        String path = finalFile.toString();

        String filename = path.substring(path.lastIndexOf("/") + 1);

        pathList.add(path);
        imgList.add(filename);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_confirm:
                confirmActionComplete();
                break;
        }

        return super.onOptionsItemSelected(item);

    }
}
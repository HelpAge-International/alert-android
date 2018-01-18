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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.alertpreparedness.platform.alert.R;
import org.alertpreparedness.platform.alert.dagger.DependencyInjector;
import org.alertpreparedness.platform.alert.dagger.annotation.ActionRef;
import org.alertpreparedness.platform.alert.dagger.annotation.BaseStorageRef;
import org.alertpreparedness.platform.alert.dagger.annotation.NoteRef;
import org.alertpreparedness.platform.alert.dashboard.activity.HomeScreen;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertAdapter;
import org.alertpreparedness.platform.alert.dashboard.adapter.AlertFieldsAdapter;
import org.alertpreparedness.platform.alert.dashboard.model.Tasks;
import org.alertpreparedness.platform.alert.min_preparedness.adapter.AttachmentAdapter;
import org.alertpreparedness.platform.alert.min_preparedness.fragment.MinPreparednessFragment;
import org.alertpreparedness.platform.alert.min_preparedness.model.Action;
import org.alertpreparedness.platform.alert.min_preparedness.model.Notes;
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

    ArrayList<String> imgList = new ArrayList<>();
    ArrayList<String> pathList = new ArrayList<>();

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

    private void confirmActionComplete() {
        String notes = editTextNote.getText().toString().trim();

        if (TextUtils.isEmpty(notes)) {
            SnackbarHelper.show(this, getString(R.string.txt_err_complete_action_note));
            return;
        }

        if (imgList.size() == 0) {
            SnackbarHelper.show(this, getString(R.string.txt_err_add_attachments));
        }

        saveData(notes);
    }

    private void saveData(String texts) {
        Intent intent = getIntent();
        String key = intent.getStringExtra("ACTION_KEY");
        String userID = PreferHelper.getString(getApplicationContext(), Constants.AGENCY_ID);

        saveNote(texts, key);
        StorageReference riversRef = mStorageRef.child("documents/" + userID + "/" + key + "/images/");

        for (int i = 0; i < pathList.size(); i++) {
            System.out.println("Uri.parse(pathList.get(i)) = " + Uri.parse(pathList.get(i)));
            final int finalI = i;
            riversRef.putFile(Uri.parse("file://" + pathList.get(i)))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "Action completed successfully", Toast.LENGTH_LONG).show();
                            editTextNote.setText("");
                            imgList.remove(finalI);
                            simpleAdapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            System.out.println("exception = " + exception);
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
                        break;
                }
                return false;
            }
        }).show();
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
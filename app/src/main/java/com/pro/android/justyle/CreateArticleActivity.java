package com.pro.android.justyle;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.util.Objects;
import static com.pro.android.justyle.FrontPageActivity.isWifiConn;
import static com.pro.android.justyle.FrontPageActivity.userUid;
import static com.pro.android.justyle.WardrobeFragment.mImageUri;


public class CreateArticleActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    private EditText mArticleText;
    private EditText mArticleName;
    private ProgressBar mProgressBar;
    private TextView mUserName;
    private String mAction;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_article);
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mFirebaseAuth.getCurrentUser();
        mArticleText = findViewById(R.id.ArticleTextId);
        mArticleName = findViewById(R.id.ArticleNameId);
        ImageView mArticleImg = findViewById(R.id.articleImageId);
        mUserName = findViewById(R.id.createUserName);
        mUserName.setText(Objects.requireNonNull(mCurrentUser).getEmail());

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this, R.array.action, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Picasso.get()
                .load(mImageUri)
                .resize(600,600)
                .centerCrop()
                .into(mArticleImg);

        Button mCreateButton = findViewById(R.id.buttonCreateArticleId);
        mProgressBar = findViewById(R.id.progressBarId);
        mStorageRef = FirebaseStorage.getInstance().getReference(userUid);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(userUid);
        mCreateButton.setOnClickListener(new View.OnClickListener() {

            /**
             *
             * @param v is the position that the onClick requires on the UI for the method
             *          to be called onClick method calls a method when its corresponding UI item is
             *          pressed, this onClick calls the uploadFile() method if upload if the device
             *          is connected to wifi and upload task is finished
             */
            @Override
            public void onClick(View v) {

                if (isWifiConn || MoreActivity.mWifiCare) {

                    if (mUploadTask != null && mUploadTask.isInProgress()) {
                        Toast.makeText(CreateArticleActivity.this,
                                "Upload in progress", Toast.LENGTH_SHORT).show();
                    } else {
                        uploadFile();
                    }
                }
                else {
                    Toast.makeText(CreateArticleActivity.this,
                            "You should be connected to wifi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * getFileExtension method gets file extension from the image
     * @param uri is the file uri of the image
     * @return
     */
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    /**
     * uploadFile method creates storage reference
     * contains a progress bar
     * sets mArticleName, mArticleText, mUserName and mAction to DatabaseRef
     */

    private void uploadFile() {
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 100);

                            Toast.makeText(CreateArticleActivity.this,
                                    "Upload successful", Toast.LENGTH_LONG).show();

                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()); //needed

                            Uri downloadUrl = urlTask.getResult();

                            Upload upload = new Upload(mArticleName.getText().toString().trim(),
                                    Objects.requireNonNull(downloadUrl).toString(),mArticleText
                                    .getText().toString().trim(), mUserName.getText().toString()
                                    .trim(),mAction);

                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(Objects.requireNonNull(uploadId)).setValue(upload);
                            openMyWardrobeActivity(); // start new activity
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateArticleActivity.this, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() /
                                    taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * opens MyWardrobeActivity with an intent
     */
    private void openMyWardrobeActivity(){
        finish();
        Intent intent = new Intent(this, MyWardrobeActivity.class);
        startActivity(intent);
    }

    /**
     * onItemSelected method is for spinner containing 4 switch cases
     * @param parent
     * @param view
     * @param position is the position of the image
     * @param id is the id of the image
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
         String aPosition = parent.getItemAtPosition(position).toString();
        switch (aPosition){
            case "To Sell":

                mAction = "Selected as To Sell";
                Toast.makeText(parent.getContext(),mAction, Toast.LENGTH_SHORT).show();
                break;

            case "To Rent":
                mAction = "Selected as To Rent";
                Toast.makeText(parent.getContext(),mAction, Toast.LENGTH_SHORT).show();
                break;

            case "To Lend":
                mAction = "Selected as To Lend";
                Toast.makeText(parent.getContext(),mAction, Toast.LENGTH_SHORT).show();
                break;

            case "Not Selected":
                mAction = "Not Selected";
                Toast.makeText(parent.getContext(),mAction, Toast.LENGTH_SHORT).show();
                break;
            }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}





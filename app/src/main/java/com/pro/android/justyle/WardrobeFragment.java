package com.pro.android.justyle;

import android.Manifest;
import android.content.Intent;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.pro.android.justyle.FrontPageActivity.bpt;


public class WardrobeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "WardrobeFragment";
    private static final int PICK_IMAGE_REQUEST = 234;
    private static final int REQUEST_PICTURE_CAPTURE = 1;


    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Button chooseButton;
    private static ImageView mImageView;
    static Uri mImageUri;
    private String pictureFilePath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_wardrobe, container, false);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();
        TextView mTextViewNickname = v.findViewById(R.id.Nickname);
        Button uploadButton = v.findViewById(R.id.uploadButtonId);
        chooseButton =  v.findViewById(R.id.ChooseButtonId);
        Button takePicButton = v.findViewById(R.id.takePicId);

        mImageView = v.findViewById(R.id.imageViewId);

        chooseButton.setOnClickListener(this);

        takePicButton.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param v is the position that the onClick requires on the UI for the method to be
             *          called onClick method calls a method when its corresponding UI item is
             *          pressed this onClick calls the method dispatchPictureTakenAction if the user
             *          accepts that the application is allowed to use the camera and there is a
             *          camera in the phone
             */
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=23) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                }

                if (bpt > 10 || MoreActivity.mBatteryCare) { //batter level
                    dispatchPictureTakenAction();
                }
                else {
                    Toast.makeText(getContext(), "Your battery should be more than 10%",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImageView.getDrawable() != null){

                    Intent intent = new Intent(getActivity(), CreateArticleActivity.class);
                    startActivity(intent);
                    Objects.requireNonNull(getFragmentManager()).popBackStack();
                }else if(mImageView.getDrawable() == null){
                    Toast.makeText(getContext(), "An image must be picked or taken",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        mFirebaseFirestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot documentSnapshot :
                            Objects.requireNonNull(task.getResult())){
                        Log.d(TAG, documentSnapshot.getId()+ " => " +documentSnapshot.getData());
                    }
                }else{
                    Log.w(TAG, "Error getting document", task.getException());
                }
            }
        });

        String userID = getString(getId());
        DocumentReference mDocumentReference = mFirebaseFirestore.collection
                ("users").document(userID);

        mTextViewNickname.setText(Objects.requireNonNull(user).getEmail());

        return v;
    }

    private void showFileChoose(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select an image"),
                PICK_IMAGE_REQUEST);
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            mImageUri = data.getData();

            //imageView.setImageURI(mImageUri);
            Picasso.get()
                    .load(mImageUri)
                    .resize(600,600)
                    .centerCrop()
                    .into(mImageView);

        }
        else if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == RESULT_OK &&
                data != null && data.getData() != null){
            Picasso.get()
                    .load(pictureFilePath)
                    .into(mImageView);
        }
    }

    /**
     *
     * @param v is the position that the onClick requires on the UI for the method to be called
     *          onClick method calls a method when its corresponding UI item is pressed
     *          this onClick calls the showFileChoose method
     */
    @Override
    public void onClick(View v) {
        if (v == chooseButton ) {

            showFileChoose();
            //open camera roll
        }
    }

    /**
     * the dispatchPictureAction method calls the camera to take a picture and then it saves that
     * image in the phones local gallery by calling addToGallery method
     */
    private void dispatchPictureTakenAction() {
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePic.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        if (takePic.resolveActivity(Objects.requireNonNull(getActivity())
                .getPackageManager()) != null) {
            startActivityForResult(takePic, REQUEST_PICTURE_CAPTURE);

            File pictureFile = null;
            try {
                pictureFile = getPictureFile();
            }catch (IOException e){
                Toast.makeText(getContext(), "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (pictureFile != null){
                Uri photoURI = FileProvider.getUriForFile(getActivity().getBaseContext(),
                        "com.pro.android.justyle.fileprovider",pictureFile);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(takePic, REQUEST_PICTURE_CAPTURE);
                addToGallery();

            }
        }
    }

    /**
     *
     * @return getPictureFile returns the image that it is told to get
     * @throws IOException is the exception that can be caused by the internal calls of this method
     * this method finds the image taken by the camera
     */

    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "image_" + timeStamp;
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile,  ".jpg", storageDir);
        pictureFilePath = image.getAbsolutePath();
        return image;
    }

    /**
     * addToGallery creates and saves a file in the phones gallery from the image taken by the
     * camera in the dispatchPictureTakenAction method
     */
    private void addToGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(pictureFilePath);
        Uri picUri = Uri.fromFile(f);
        galleryIntent.setData(picUri);
        Objects.requireNonNull(getActivity()).sendBroadcast(galleryIntent);
    }
}
package com.pro.android.justyle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import java.util.Objects;

public class ViewMyWardrobeArticleActivity extends AppCompatActivity {
    private TextView mArticleName;
    private TextView mArticleDescription;
    private TextView mArticleUserName;
    private ImageView mArticleImage;
    private TextView mAction;
    private  ImageAdapter mAdapter;
    private String mArticleNameString;
    private String mArticleDescriptionString;
    private String mArticleImageString;
    private String mUserName;
    private String mActionString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_article);
        mArticleName = findViewById(R.id.nameViewId);
        mArticleDescription = findViewById(R.id.descriptionViewId);
        mArticleImage = findViewById(R.id.articleView);
        mArticleUserName = findViewById(R.id.userNameViewId);
        mAction = findViewById(R.id.actionId);
        /**
         * gets the data from database child under unique userUid
         * gets the key from string "item_wardrobe_key"
         */
        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference()
                .child(FrontPageActivity.userUid);
        String mPostKey = Objects.requireNonNull(getIntent().getExtras())
                .getString("item_wardrobe_key");

        mDatabaseRef.child(Objects.requireNonNull(mPostKey)).addValueEventListener
                (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /**
                 * onDataChange() method gets value from the Database's childs and sets the TextViews
                 * Picasso.get() method gets the image URL string and sets it to ImageView
                 */

                mArticleNameString = (String) dataSnapshot.child("name").getValue();
                mArticleDescriptionString = (String) dataSnapshot.child("description").getValue();
                mArticleImageString = (String) dataSnapshot.child("imageUrl").getValue();
                mUserName = (String) dataSnapshot.child("userName").getValue();
                mActionString= (String) dataSnapshot.child("action").getValue();
                mArticleName.setText(mArticleNameString);
                mArticleDescription.setText(mArticleDescriptionString);
                mArticleUserName.setText(mUserName);
                mAction.setText(mActionString);

                Picasso.get()
                        .load(mArticleImageString)
                        .resize(500,500)
                        .centerCrop()
                        .into(mArticleImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
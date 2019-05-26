package com.pro.android.justyle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.pro.android.justyle.FrontPageActivity.userUid;

public class MyWardrobeActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener{



    private ImageAdapter mAdapter;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private MaterialSearchView searchView; //needed at onCreateOptionsMenu
    private RecyclerView mRecyclerView;
    private List<Upload> mUploads;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_wardrobe);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUploads = new ArrayList<>();
        mAdapter = new ImageAdapter(MyWardrobeActivity.this, mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(MyWardrobeActivity.this);

        EditText editText = findViewById(R.id.searchView);
        // Search bar, TextWatcher allow us to instantly update data on other views
        editText.addTextChangedListener(new TextWatcher() {
            /**
             * the standard beforeTextChanged method
             * it does nothing as the code is interested in after the change
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // The count characters beginning at start are about to be replaced
            }

            /**
             * the standard onTextChanged method
             * it does nothing as the code is interested in after the change
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //  The characters beginning at start have just replaced old text
            }

            /**
             *
             * @param s is the input of the EditText field editText
             * when the text is changed the afterTextChanged method will call the search method
             */
            @Override
            public void afterTextChanged(Editable s) {
                // The text has been changed
                if (!s.toString().isEmpty()) {
                   search(s.toString());
                } else {
                }
            }
        });

        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(userUid);
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    Objects.requireNonNull(upload).setKey(postSnapshot.getKey());
                    mUploads.add(upload);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(MyWardrobeActivity.this, databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Search bar, query is linked with the database and
    private void search(String toString) {
        Query query = mDatabaseRef.orderByChild("name")
                .startAt(toString)
                .endAt(toString+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    mUploads.clear();
                    for(DataSnapshot dss: dataSnapshot.getChildren())
                    {
                        final Upload upload = dss.getValue(Upload.class);
                        mUploads.add(upload);
                    }
                    ImageAdapter imageAdapter = new ImageAdapter(getApplicationContext(),
                            mUploads);
                    mRecyclerView.setAdapter(imageAdapter);
                    imageAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "View article "+ position, Toast.LENGTH_SHORT).show();

            //Gets the article Key from Firebase
            String mPostKey = mUploads.get(position).getKey();
            //Sends the article Key to the viewArticle activity
            Intent viewActivityIntent = new Intent(MyWardrobeActivity.this,
                    ViewMyWardrobeArticleActivity.class);
            viewActivityIntent.putExtra("item_wardrobe_key", mPostKey);
            startActivity(viewActivityIntent);
        }
    @Override
    public void sendToMarketClick(int position) {
        Toast.makeText(this, "Send to market, click at position "+ position,
                Toast.LENGTH_SHORT).show();

        Upload selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        // copy to Firebase database under marketplace folder
        Firebase.setAndroidContext(this);
        Firebase ref = new Firebase("https://justyle-1.firebaseio.com/");
        Map newPost = new HashMap();
        newPost.put("name", selectedItem.getName());
        newPost.put("imageUrl", selectedItem.getImageUrl());
        newPost.put("description", selectedItem.getDescription());
        newPost.put("action", selectedItem.getAction());
        newPost.put("userName", selectedItem.getUserName());
        Map updatedUserData = new HashMap();
        updatedUserData.put("marketplace/" + selectedKey, newPost);
        // Do a deep-path update
        ref.updateChildren(updatedUserData, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    System.out.println("Error updating data: " + firebaseError.getMessage());
                }
            }
        });
    }
    @Override
    public void onDeleteClick(int position) {
        Upload selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(MyWardrobeActivity.this, "Article deleted",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_marketplace, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }
}
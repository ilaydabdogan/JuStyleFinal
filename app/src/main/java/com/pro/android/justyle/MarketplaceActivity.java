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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MarketplaceActivity extends AppCompatActivity
        implements ImageAdapter.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<Upload> mUploads;
    private MaterialSearchView searchView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marketplace);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        EditText editText = findViewById(R.id.searchView);
        /**
         * It is used for the Search bar, TextWatcher allow to instantly update data on other views
         */
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // The count characters beginning at start are about to be replaced
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //  The characters beginning at start have just replaced old text
            }

            @Override
            public void afterTextChanged(Editable s) {
                // The text has been changed
                if (!s.toString().isEmpty()) {
                    search(s.toString());
                }
            }

        });

        mUploads = new ArrayList<>();
        mAdapter = new ImageAdapter(MarketplaceActivity.this, mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(MarketplaceActivity.this);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("marketplace");
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

                Toast.makeText(MarketplaceActivity.this, databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });                                                                
    }
    /**
     * Search bar, query is linked with the database
     * @param toString is used to convert database child name datas to String
     */
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
                 ImageAdapter imageAdapter = new ImageAdapter(getApplicationContext(), mUploads);
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
        /**
         * Gets the article Key from Firebase
         * Sends the article Key to the viewArticle activity
         */

        String mPostKey = mUploads.get(position).getKey();
        Intent viewActivityIntent = new Intent(MarketplaceActivity.this,
                ViewArticleActivity.class);
        viewActivityIntent.putExtra("item_key", mPostKey);
        startActivity(viewActivityIntent);
    }

    /**
     * these are not used but implemented as this activity implements ImageAdapter class
     * @param position
     */

    @Override
    public void sendToMarketClick(int position) { }

    @Override
    public void onDeleteClick(int position) { }

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










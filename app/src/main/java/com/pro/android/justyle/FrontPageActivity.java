package com.pro.android.justyle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class FrontPageActivity extends AppCompatActivity implements FragmentActionListener {

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    public static String userUid;
    @SuppressLint("StaticFieldLeak")
    private static View frontView;
    static boolean isWifiConn = false;
    static double bpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);
        mFragmentManager = getSupportFragmentManager();
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        Button mActivityButton = findViewById(R.id.ActivityButton);
        Button mProfileButton = findViewById(R.id.ProfileButton);
        Button mMoreButton = findViewById(R.id.MoreButton);
        ImageButton mCameraButton = findViewById(R.id.CameraButton);
        frontView = findViewById(R.id.fpView);
        frontView.setBackgroundColor(getResources().getColor(ProfileActivity.mBackgroundColor));
        updateView();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userUid = Objects.requireNonNull(currentUser).getUid();

        /**
         * checks for the wi fi connection
         */
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        for (Network network : connMgr.getAllNetworks()){
            NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                isWifiConn |= networkInfo.isConnected();
            } else{
                isWifiConn = false;
            }
        }
        /**
         * checks for the battery level
         */
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);
        assert batteryStatus != null;
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        bpt = (level / (double) scale)*100; //battery percentage

        mProfileButton.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param v is the position that the onClick requires on the UI for the method to be called
             *          onClick method calls a method when its corresponding UI item is pressed
             *          this onClick starts the ProfileActivity
             */
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FrontPageActivity.this,ProfileActivity.class));
            }
        });

        mActivityButton.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param v is the position that the onClick requires on the UI for the method to be called
             *          onClick method calls a method when its corresponding UI item is pressed
             *          this onClick restarts the FrontPageActivity
             */
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(FrontPageActivity.this,FrontPageActivity.class));

            }
        });
        mMoreButton.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param v is the position that the onClick requires on the UI for the method to be called
             *          onClick method calls a method when its corresponding UI item is pressed
             *          this onClick starts the MoreActivity
             */
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FrontPageActivity.this,MoreActivity.class));
            }
        });
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param v is the position that the onClick requires on the UI for the method to be called
             *          onClick method calls a method when its corresponding UI item is pressed
             *          this onClick calls the addWardrobeFragment method
             */
            @Override
            public void onClick(View v) {
                addWardrobeFragment();
            }
        });

        if (mFirebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        addFrontPageFragment();
    }
    /**
     * addFrontPageFragment changes the current fragment in the FrontPageActivity to
     * the FrontPageFragment, it is called when the activity starts
     */
    private void addFrontPageFragment(){
        mFragmentTransaction = mFragmentManager.beginTransaction();

        FrontPageFragment frontPageFragment = new FrontPageFragment();
        frontPageFragment.setFragmentActionListener(this);

        mFragmentTransaction.add(R.id.fragment_container, frontPageFragment);
        mFragmentTransaction.commit();

    }

    /**
     * addWardrobeFragment changes the current fragment in the FrontPageActivity to
     * the WardrobeFragment
     */
    private void addWardrobeFragment(){
        mFragmentTransaction = mFragmentManager.beginTransaction();

        WardrobeFragment wardrobeFragment = new WardrobeFragment();

        Bundle bundle = new Bundle();
        wardrobeFragment.setArguments(bundle);

        mFragmentTransaction.replace(R.id.fragment_container, wardrobeFragment);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.commit();
    }

    /**
     * updateView updates the background color of the activity
     */
    private void updateView(){
        frontView.setBackgroundColor(getResources().getColor(ProfileActivity.mBackgroundColor));
    }

    /**
     * method onWardrobeFragmentClicked is overridden from the Interface FragmentActionListener
     * it states that when the method is called it will open the MyWardrobeActivity
     */
    @Override
    public void onWardrobeFragmentClicked() {
        startActivity(new Intent(FrontPageActivity.this,MyWardrobeActivity.class));
    }

    /**
     * method onMarketplaceFragmentClicked is overridden from the Interface FragmentActionListener
     * it states that when the method is called it will open the MarketplaceActivity
     */
    @Override
    public void onMarketplaceFragmentClicked(){
        startActivity(new Intent(FrontPageActivity.this,MarketplaceActivity.class));
    }
}

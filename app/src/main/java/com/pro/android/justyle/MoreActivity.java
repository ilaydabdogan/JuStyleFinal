package com.pro.android.justyle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MoreActivity extends AppCompatActivity {

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String WIFITOGGLE = "wifiButton";
    private static final String BATTERYTOGGLE = "batteryButton";

    private Button mWifiButton;
    private Button mBatteryButton;
    public static boolean mWifiCare;
    public static boolean mBatteryCare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        mWifiButton = findViewById(R.id.Wifi_Care_Button);
        mBatteryButton = findViewById(R.id.Battery_Care_Button);
        mWifiButton.setText(R.string.OFF);
        mWifiButton.setTextColor(getResources().getColor(R.color.textColorDark));
        mWifiButton.setBackgroundColor(getResources().getColor(R.color.red));
        mBatteryButton.setText(R.string.OFF);
        mBatteryButton.setTextColor(getResources().getColor(R.color.textColorDark));
        mBatteryButton.setBackgroundColor(getResources().getColor(R.color.red));

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        mBatteryButton.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param v is the position that the onClick requires on the UI for the method to be called
             * onClick method calls a method when its corresponding UI item is pressed
             * this onClick changes the value of mBatteryCare and the look of the button pressed from
             *          on to off, or from off to on
             *          it then calls the method saveData and updateView
             */
            @Override
            public void onClick(View v) {
                if (mBatteryButton.getText().equals("OFF")){
                    mBatteryButton.setText(R.string.ON);
                    mBatteryButton.setBackgroundColor(getResources().getColor(R.color.green));
                    mBatteryCare = true;

                }else if (mBatteryButton.getText().equals("ON")){
                    mBatteryButton.setText(R.string.OFF);
                    mBatteryButton.setBackgroundColor(getResources().getColor(R.color.red));
                    mBatteryCare = false;
                }
                saveData();
                updateView();
            }
        });
        loadData();

        mWifiButton.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * * @param v is the position that the onClick requires on the UI for the method to be called
             *   onClick method calls a method when its corresponding UI item is pressed
             *   this onClick changes the value of mWifiCare and the look of the button pressed from
             *   on to off, or from off to on
             *   it then calls the method saveData and updateView
             */
            @Override
            public void onClick(View v) {
                if (mWifiButton.getText().equals("OFF")){
                    mWifiButton.setText(R.string.ON);
                    mWifiButton.setBackgroundColor(getResources().getColor(R.color.green));
                    mWifiCare = true;

                }else if (mWifiButton.getText().equals("ON")){
                    mWifiButton.setText(R.string.OFF);
                    mWifiButton.setBackgroundColor(getResources().getColor(R.color.red));
                    mWifiCare = false;
                }
                saveData();
                updateView();
            }
        });
        loadData();
    }

    /**
     * saveData uses SharedPreferences to save the state if the mWifiCare and mBatteryCare
     */
    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(WIFITOGGLE, mWifiCare);
        editor.putBoolean(BATTERYTOGGLE, mBatteryCare);
        editor.apply();
    }

    /**
     * loadData gets the values of the mWifiCare and mBatteryCare from their saved state made with
     * the saveData method and the calls the updateView
     */
    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        mWifiCare = sharedPreferences.getBoolean(WIFITOGGLE,false);
        mBatteryCare = sharedPreferences.getBoolean(BATTERYTOGGLE, false);
        updateView();
    }

    /**
     * updateView sets the look of the MoreActivity to the appropiate look depending on the on/off
     * state of mWifiCare and mBatteryCare.
     */
    private void updateView() {
        if (mWifiCare) {
            mWifiButton.setText(R.string.ON);
            mWifiButton.setBackgroundColor(getResources().getColor(R.color.green));
        } else if (!mWifiCare) {
            mWifiButton.setText(R.string.OFF);
            mWifiButton.setBackgroundColor(getResources().getColor(R.color.red));
        }
        if (mBatteryCare) {
            mBatteryButton.setText(R.string.ON);
            mBatteryButton.setBackgroundColor(getResources().getColor(R.color.green));
        } else if (!mBatteryCare) {
            mBatteryButton.setText(R.string.OFF);
            mBatteryButton.setBackgroundColor(getResources().getColor(R.color.red));

        }
    }
}

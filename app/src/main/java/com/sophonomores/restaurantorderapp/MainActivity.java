package com.sophonomores.restaurantorderapp;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.sophonomores.restaurantorderapp.services.Advertiser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions(new String[] {
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, 1);
    }

    public void goToCustomerMainActivity(View view) {
        Intent intent = new Intent(this, CustomerMainActivity.class);
        startActivity(intent);
//        new Discoverer(MainActivity.this).startDiscovery();
    }

    public void goToMerchantMainActivity(View view) {
        new Advertiser(MainActivity.this).startAdvertising();
        Intent intent = new Intent(this, MerchantMainActivity.class);
        startActivity(intent);
    }

}
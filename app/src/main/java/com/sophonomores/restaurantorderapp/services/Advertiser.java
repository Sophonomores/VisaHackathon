package com.sophonomores.restaurantorderapp.services;

import android.content.Context;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.Strategy;

public class Advertiser {

    private final Context mContext;
    private final String DEVICE_NAME = "MERCHANT_APP"; // hardcoded for now
    private final String SERVICE_ID = "com.sophonomores.restaurantorderapp"; // hardcoded for now
    private final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionCallback();

    public Advertiser(Context context) {
        mContext = context;
    }

    public void startAdvertising() {
        AdvertisingOptions advertisingOptions =
                new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build();
        Nearby.getConnectionsClient(mContext)
                .startAdvertising(
                        DEVICE_NAME, SERVICE_ID, connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            System.out.println("We are advertising!");
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            System.err.println("We were unable to start advertising: " + e.getMessage());
                        });
    }
}

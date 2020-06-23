package com.sophonomores.restaurantorderapp.services;

import android.content.Context;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Strategy;

import androidx.annotation.NonNull;

public class Discoverer {

    private final Context mContext;
    private final String DEVICE_NAME = "CLIENT_APP"; // hardcoded for now
    private final String SERVICE_ID = "com.sophonomores.restaurantorderapp"; // hardcoded for now
    private final EndpointDiscoveryCallback clientDiscoveryCallback = new ClientDiscoveryCallback();

    public Discoverer(Context context) {
        mContext = context;
    }

    public void startDiscovery() {
        DiscoveryOptions discoveryOptions =
                new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build();
        Nearby.getConnectionsClient(mContext)
                .startDiscovery(SERVICE_ID, clientDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            System.out.println("We are discovering!");
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            System.err.println("We were unable to start discovering: " + e.getMessage());
                        });
    }
}

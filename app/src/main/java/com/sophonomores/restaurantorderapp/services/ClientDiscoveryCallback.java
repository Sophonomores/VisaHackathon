package com.sophonomores.restaurantorderapp.services;

import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;

import androidx.annotation.NonNull;

public class ClientDiscoveryCallback extends EndpointDiscoveryCallback {
    @Override
    public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
        String message = String.format("%s: Endpoint found: %s", endpointId, discoveredEndpointInfo.toString());
        System.out.println(message);
    }

    @Override
    public void onEndpointLost(@NonNull String endpointId) {
        System.out.println(endpointId + ": Endpoint lost.");
    }
}

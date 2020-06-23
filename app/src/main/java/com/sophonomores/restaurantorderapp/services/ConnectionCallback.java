package com.sophonomores.restaurantorderapp.services;

import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;

import androidx.annotation.NonNull;

public class ConnectionCallback extends ConnectionLifecycleCallback {
    @Override
    public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
        String message = String.format("%s: Connection initiated: %s", endpointId, connectionInfo.toString());
        System.out.println(message);
    }

    @Override
    public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution connectionResolution) {
        String message = String.format("%s: Connection result: %s", endpointId, connectionResolution.toString());
        System.out.println(message);
    }

    @Override
    public void onDisconnected(@NonNull String endpointId) {
        System.out.println(endpointId + ": Disconnected.");
    }
}

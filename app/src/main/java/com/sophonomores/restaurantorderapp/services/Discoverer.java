package com.sophonomores.restaurantorderapp.services;

import android.content.Context;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import androidx.annotation.NonNull;

public class Discoverer {

    private final Context context;
    public static final String DEVICE_NAME = "CLIENT_APP"; // hardcoded for now
    private static final String SERVICE_ID = "com.sophonomores.restaurantorderapp"; // hardcoded for now

    private final List<String> devices;

    public Discoverer(Context context) {
        this.context = context;
        devices = new ArrayList<>();
    }

    public List<String> getDevices() {
        return devices;
    }

    public void startDiscovery(Consumer<String> callback) {
        DiscoveryOptions discoveryOptions =
                new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build();
        Nearby.getConnectionsClient(context)
                .startDiscovery(SERVICE_ID, new ClientDiscoveryCallback(callback), discoveryOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            System.out.println("We are discovering!");
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            System.err.println("We were unable to start discovering: " + e.getMessage());
                        });
    }

    private class ClientDiscoveryCallback extends EndpointDiscoveryCallback {
        private Consumer<String> callback;

        public ClientDiscoveryCallback(Consumer<String> callback) {
            this.callback = callback;
        }

        @Override
        public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            String message = String.format("%s: Endpoint found: %s", endpointId, discoveredEndpointInfo.toString());
            System.out.println(message);
            if (!devices.contains(endpointId)) {
                devices.add(endpointId);
                callback.accept(endpointId);
                // Use these to make API calls in the app:

                // GET:
                // new Messenger(context, DEVICE_NAME).get(endpointId, ResourceURIs.MENU, System.out::println);

                // POST:
                // new Messenger(context, DEVICE_NAME).post(endpointId,
                //        ResourceURIs.CHECKOUT,
                //        "My order: ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                //        System.out::println);
            }
        }

        @Override
        public void onEndpointLost(@NonNull String endpointId) {
            System.out.println(endpointId + ": Endpoint lost.");
            devices.remove(endpointId);
        }
    }
}

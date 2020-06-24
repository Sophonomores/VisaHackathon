package com.sophonomores.restaurantorderapp.services;

import android.content.Context;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Strategy;
import com.sophonomores.restaurantorderapp.services.api.ApiEndpoint;
import com.sophonomores.restaurantorderapp.services.api.ResourceURIs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;

public class Discoverer {

    private final Context context;
    public final String DEVICE_NAME = "CLIENT_APP"; // hardcoded for now
    private final String SERVICE_ID = "com.sophonomores.restaurantorderapp"; // hardcoded for now
    private final EndpointDiscoveryCallback clientDiscoveryCallback = new ClientDiscoveryCallback();

    private final List<String> devices;

    public Discoverer(Context context) {
        this.context = context;
        devices = new ArrayList<>();
    }

    public List<String> getDevices() {
        List<String> result = new ArrayList<>();
        Collections.copy(result, devices);
        return result;
    }

    public void startDiscovery() {
        DiscoveryOptions discoveryOptions =
                new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build();
        Nearby.getConnectionsClient(context)
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

    private class ClientDiscoveryCallback extends EndpointDiscoveryCallback {
        @Override
        public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            String message = String.format("%s: Endpoint found: %s", endpointId, discoveredEndpointInfo.toString());
            System.out.println(message);
            if (!devices.contains(endpointId)) {
                devices.add(endpointId);
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

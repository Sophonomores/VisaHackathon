package com.sophonomores.restaurantorderapp.services;

import android.content.Context;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.sophonomores.restaurantorderapp.services.api.ApiEndpoint;
import com.sophonomores.restaurantorderapp.services.api.ApiException;
import com.sophonomores.restaurantorderapp.services.api.Parser;
import com.sophonomores.restaurantorderapp.services.api.Request;

import androidx.annotation.NonNull;

public class Advertiser {

    private final Context context;
    private final String DEVICE_NAME = "MERCHANT_APP"; // hardcoded for now
    private final String SERVICE_ID = "com.sophonomores.restaurantorderapp"; // hardcoded for now
    private final ConnectionLifecycleCallback connectionLifecycleCallback = new MerchantConnectionCallback();
    private final PayloadCallback payloadCallback = new MerchantPayloadCallback();

    public Advertiser(Context context) {
        this.context = context;
    }

    public void startAdvertising() {
        System.out.println(ConnectionsClient.MAX_BYTES_DATA_SIZE);
        AdvertisingOptions advertisingOptions =
                new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build();
        Nearby.getConnectionsClient(context)
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

    private class MerchantConnectionCallback extends ConnectionLifecycleCallback {
        @Override
        public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
            String message = String.format("%s: Connection initiated: %s", endpointId, connectionInfo.toString());
            System.out.println(message);
            Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback);
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

    private class MerchantPayloadCallback extends PayloadCallback {
        @Override
        public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
            String message = String.format("%s: Payload received in bytes: %s", endpointId, new String(payload.asBytes()));
            System.out.println(message);
            Request request = Parser.parseRequest(new String(payload.asBytes()));
            String response;
            try {
                response = ApiEndpoint.getAction(request.getUri(), request.getMethod()).execute(request.getContent());
            } catch (ApiException e) {
                response = e.getStatusCode();
            }
            Payload bytesPayload = Payload.fromBytes(response.getBytes());
            Nearby.getConnectionsClient(context).sendPayload(endpointId, bytesPayload);
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

        }
    }
}

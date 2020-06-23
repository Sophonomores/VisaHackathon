package com.sophonomores.restaurantorderapp.services;

import android.content.Context;
import android.telecom.Call;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.tasks.Task;
import com.sophonomores.restaurantorderapp.services.api.ApiEndpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

import androidx.annotation.NonNull;

public class Messenger {

    private final Context context;
    private final String deviceName;

    public Messenger(Context context, String deviceName) {
        this.context = context;
        this.deviceName = deviceName;
    }

    public void get(String endpointId, String uri, Consumer<String> callback) {
        String message = "GET:" + uri;
        Nearby.getConnectionsClient(context)
                .requestConnection(deviceName, endpointId, new ConnectionCallback(message, callback))
                .addOnFailureListener(
                        (Exception e) -> {
                            throw new MessengerException("Failed to request the connection.");
                        });
    }

    private class ConnectionCallback extends ConnectionLifecycleCallback {
        private final String message;
        private final Consumer<String> callback;

        ConnectionCallback(String message, Consumer<String> callback) {
            this.message = message;
            this.callback = callback;
        }

        @Override
        public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
            Nearby.getConnectionsClient(context).acceptConnection(endpointId, new MessengerPayloadCallback(callback));
        }

        @Override
        public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution connectionResolution) {
            switch (connectionResolution.getStatus().getStatusCode()) {
                case ConnectionsStatusCodes.STATUS_OK:
                    Payload bytesPayload = Payload.fromBytes(message.getBytes());
                    Nearby.getConnectionsClient(context).sendPayload(endpointId, bytesPayload);
                    break;
                case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                    // The connection was rejected by one or both sides.
                    break;
                case ConnectionsStatusCodes.STATUS_ERROR:
                    // The connection broke before it was able to be accepted.
                    break;
                default:
                    // Unknown status code
            }
        }

        @Override
        public void onDisconnected(@NonNull String endpointId) {

        }
    }

    private class MessengerPayloadCallback extends PayloadCallback {
        private Consumer<String> callback;

        MessengerPayloadCallback(Consumer<String> callback) {
            this.callback = callback;
        }

        @Override
        public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
            callback.accept(new String(payload.asBytes()));
            Nearby.getConnectionsClient(context).disconnectFromEndpoint(endpointId);
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

        }
    }

    class MessengerException extends RuntimeException {
        String message;

        public MessengerException(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "MessengerException: " + this.message;
        }
    }
}

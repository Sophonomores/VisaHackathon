package com.sophonomores.restaurantorderapp.vpp;

import android.util.Base64;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VppRequest extends JsonObjectRequest {
    private static final String BASIC_AUTH_USERNAME = "Z3TILZ6P9UBKKX51X4GA21c4mjn0tkbcVT5330DsB5Ut8BOXA";
    private static final String BASIC_AUTH_PASSWORD = "J0e4fv8xYKu82Q33Tc7jJUe7wZ86B0Cr";
    private static final String MLE_KEY_ID = "8a4f15b3-0545-400b-a2f0-6a52c5c88df4";

    public VppRequest(int method, String url, @Nullable JSONObject jsonRequest, Response.Listener<JSONObject> listener) {
        // Adds a default error handler
        super(method, url, jsonRequest, listener, error -> {
            System.out.println("Receive an error code!!!");
            System.out.println(error.getCause());
        });
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        String credential = BASIC_AUTH_USERNAME + ":" + BASIC_AUTH_PASSWORD;
        String encodedCredential = Base64.encodeToString(credential.getBytes(), Base64.NO_WRAP);

        Map<String, String> currentHeader = new HashMap<>(super.getHeaders());
        // Add basic authentication to the header
        currentHeader.put("Authorization", "Basic " + encodedCredential);

        // Add Key Id header for MLE
        currentHeader.put("keyId", MLE_KEY_ID);
        return currentHeader;
    }
}

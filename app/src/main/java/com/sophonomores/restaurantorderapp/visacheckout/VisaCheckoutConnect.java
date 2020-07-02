package com.sophonomores.restaurantorderapp.visacheckout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * VisaCheckoutConnect allow convenient access to Visa Checkout API.
 * Currently, only access to the update API is supported.
 */
public class VisaCheckoutConnect {
    private static final String API_KEY = "ZS3NZWIM8VE6VRIWDTN021sRrCcEVOgUnbX14E59RK3NWZM8Y";
    private static final String SHARED_SECRET = "54m59c}G7ZYYjV14XP-SUaytZd#X0MeYbMug8MWU";

    private static final String BASE_URL = "https://sandbox.api.visa.com/wallet-services-web/";
    private static final String QUERY_STRING = "apikey=" + API_KEY;

    private static String getResourcePath(String callId) {
        return "payment/info/" + callId;
    }

    private static String getUrl(String callId) {
        return BASE_URL + getResourcePath(callId) + "?" + QUERY_STRING;
    }

    /**
     * Update the order information identified by `callId`.
     * There are several kinds of update available such as confirming the order.
     * The type of updates is determined by the field in payload object.
     */
    public static void updateOrder(String callId, VisaCheckoutUpdatePayload payload,
                               Runnable onSuccess, Consumer<Integer> errorCallback) {
        RequestQueue rq = VisaCheckoutRequestQueue.getInstance().getRequestQueue();
        String url = getUrl(callId);

        // Convert payload to json
        JSONObject jsonPayload;
        try {
             jsonPayload = new JSONObject(payload.toString());
        } catch (JSONException e) {
            System.out.println("There is an error while converting payload to json");
            e.printStackTrace();
            return;
        }

        // Create listeners
        Response.Listener<JSONObject> listener = response -> {
            System.out.println("Received 200 OK! from Visa Checkout API");
            onSuccess.run();
        };

        Response.ErrorListener errorListener = error -> {
            System.out.println("Receive an error code!!!");
            if (error.networkResponse == null) {
                errorCallback.accept(500);
            } else {
                errorCallback.accept(error.networkResponse.statusCode);
            }
        };

        // Create token
        String xPayToken;
        try {
            xPayToken = generateXpaytoken(callId, jsonPayload);
        } catch (SignatureException ex) {
            System.out.println("There is an error while generating token");
            ex.printStackTrace();
            return;
        }

        // Combine everything that has been created before into a request.
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.PUT, url, jsonPayload, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>(super.getHeaders());

                headers.put("x-pay-token", xPayToken);
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");

                return headers;
            }
        };

        rq.add(req);
    }

    private static String generateXpaytoken(String callId, JSONObject payload) throws SignatureException {
        String timestamp = String.valueOf(System.currentTimeMillis()/ 1000L);

        String requestBody = payload.toString();
        String beforeHash = timestamp + getResourcePath(callId) + QUERY_STRING + requestBody;
        System.out.println("Before hash\n" + beforeHash);
        String hash = hmacSha256Digest(beforeHash, SHARED_SECRET);
        String token = "xv2:" + timestamp + ":" + hash;
        return token;
    }

    private static String hmacSha256Digest(String data, String sharedSecret)
            throws SignatureException {
        return getDigest("HmacSHA256", sharedSecret, data, true);
    }

    private static String getDigest(String algorithm, String sharedSecret, String data,  boolean toLower) throws SignatureException {
        try {
            Mac sha256HMAC = Mac.getInstance(algorithm);
            SecretKeySpec secretKey = new SecretKeySpec(sharedSecret.getBytes(StandardCharsets.UTF_8), algorithm);
            sha256HMAC.init(secretKey);

            byte[] hashByte = sha256HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String hashString = toHex(hashByte);

            return toLower ? hashString.toLowerCase() : hashString;
        } catch (Exception e) {
            throw new SignatureException(e);
        }
    }

    private static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }
}

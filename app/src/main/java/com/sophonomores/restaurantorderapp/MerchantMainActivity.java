package com.sophonomores.restaurantorderapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sophonomores.restaurantorderapp.entities.Restaurant;
import com.sophonomores.restaurantorderapp.services.Advertiser;
import com.sophonomores.restaurantorderapp.vpp.VppRequestQueue;

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

public class MerchantMainActivity extends AppCompatActivity
        implements OrderAdapter.ItemClickListener, MerchantManager.OrderListener {

    private static MerchantManager merchantManager;

    private RecyclerView orderRecyclerView;
    private RecyclerView.Adapter orderViewAdapter;
    private RecyclerView.LayoutManager orderLayoutManager;

    public static final String ORDER_INDEX = "ORDER_INDEX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_merchant);

        new Advertiser(MerchantMainActivity.this).startAdvertising();

        Restaurant restaurant = RestaurantData.makeSteakHouse(); // hardcoded

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(restaurant.getName());
        getSupportActionBar().setSubtitle("Confirmed orders");

        if (MerchantManager.isInitialised()) {
            merchantManager = MerchantManager.getInstance();
        } else {
            merchantManager = MerchantManager.init(restaurant, this);
        }

        prepareOrderRecyclerView();

        merchantManager.setOrderListener(this);
        merchantManager.startReceivingOrders();

        // Set up the request queue
        VppRequestQueue.context = this;
        VisaCheckoutRequestQueue.context = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        orderViewAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_merchant_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_view_menu) {
            Intent intent = new Intent(this, MerchantMenuActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public static MerchantManager getMerchantManager() {
        return merchantManager;
    }

    private void prepareOrderRecyclerView() {
        orderRecyclerView = (RecyclerView) findViewById(R.id.order_recycler_view);

        // add divider
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(orderRecyclerView.getContext(),
                        LinearLayoutManager.VERTICAL);
        orderRecyclerView.addItemDecoration(dividerItemDecoration);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        orderRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        orderLayoutManager = new LinearLayoutManager(this);
        orderRecyclerView.setLayoutManager(orderLayoutManager);

        // specify an adapter
        orderViewAdapter = new OrderAdapter(this, merchantManager.getOrderList());
        ((OrderAdapter) orderViewAdapter).setClickListener(this);
        orderRecyclerView.setAdapter(orderViewAdapter);
    }

    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra(ORDER_INDEX, position);
        startActivity(intent);
    }

    @Override
    public void onOrderDataChange() {
        orderViewAdapter.notifyDataSetChanged();
    }

    // TODO: Remove this function into an automated version
    public void simulateUpdateAPI(View view) {
        VisaCheckoutConnect.updateOrder("2983883160161767301", new VisaCheckoutUpdatePayload(), () -> {
            System.out.println("Update is sucessful");
        }, (errorCode) -> {
            System.out.println("Received this erro status code: " + errorCode);
        });
    }
}

/**
 * VisaCheckoutRequestQueue acts as a singleton wrapper for RequestQueue that communicates
 * with Visa Checkout API.
 */
class VisaCheckoutRequestQueue {
    private static VisaCheckoutRequestQueue instance = null;
    private RequestQueue requestQueue;
    public static Context context;

    private VisaCheckoutRequestQueue() {
        requestQueue = Volley.newRequestQueue(context);
    }

    public static synchronized VisaCheckoutRequestQueue getInstance() {
        if (instance == null) {
            instance = new VisaCheckoutRequestQueue();
        }

        return instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}

class VisaCheckoutConnect {
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
            xPayToken = generateXpaytoken(callId);
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

    private static String generateXpaytoken(String callId) throws SignatureException {
        String timestamp = String.valueOf(System.currentTimeMillis()/ 1000L);

        String requestBody = new VisaCheckoutUpdatePayload().toString();
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

/**
 * VisaCheckoutUpdatePayload build the payload for update API.
 */
class VisaCheckoutUpdatePayload {
    enum EventType {
        confirm("Confirm"), cancel("Cancel");

        // Value is the acceptable value for the json payload.
        public final String value;
        EventType(String value) {
            this.value = value;
        }
    }

    public EventType eventType = EventType.confirm;
    public Double total = 25.61;
    public String currencyCode = "USD";

    @Override
    public String toString() {
        // Note that, there should be no whitespace in payload.
        // Otherwise, generating x-pay-token will face some issues.
        return "{\"orderInfo\":" +
                "{\"currencyCode\":\"" + currencyCode + "\"," +
                "\"eventType\":\"" + eventType.value + "\"," +
                "\"total\":\"" + total + "\"}}";
    }
}

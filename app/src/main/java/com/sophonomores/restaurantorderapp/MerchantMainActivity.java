package com.sophonomores.restaurantorderapp;

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

import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

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

    private static final String API_KEY = "ZS3NZWIM8VE6VRIWDTN021sRrCcEVOgUnbX14E59RK3NWZM8Y";
    private static final String SHARED_SECRET = "54m59c}G7ZYYjV14XP-SUaytZd#X0MeYbMug8MWU";
    private static final String CALL_ID = "2983883160161767301";
    private static final String payload = "{\n" +
            "   \"orderInfo\": {\n" +
            "       \"currencyCode\": \"USD\",\n" +
            "       \"discount\": \"5.25\",\n" +
            "       \"eventType\": \"Confirm\",\n" +
            "       \"giftWrap\": \"10.1\",\n" +
            "       \"misc\": \"3.2\",\n" +
            "       \"orderId\": \"testorderID\",\n" +
            "       \"promoCode\": \"testPromoCode\",\n" +
            "       \"reason\": \"Order Successfully Created\",\n" +
            "       \"shippingHandling\": \"5.1\",\n" +
            "       \"subtotal\": \"80.1\",\n" +
            "       \"tax\": \"7.1\",\n" +
            "       \"total\": \"101\"\n" +
            "   }\n" +
            "}";

    private static final String testPayload = "{\"orderInfo\":{\"currencyCode\":\"USD\",\"eventType\":\"Confirm\",\"total\":\"25.61\"}}";

    // TODO: Remove this function into an automated version
    public void simulateUpdateAPI(View view) {
        try {
            RequestQueue rq = Volley.newRequestQueue(this);
            String url = "https://sandbox.api.visa.com/wallet-services-web/payment/info/"
                    + CALL_ID + "?apikey=" + API_KEY;
            System.out.println("Url: " + url);
            JSONObject jsonData = new JSONObject(testPayload);
            System.out.println(jsonData.toString());

            Response.Listener<JSONObject> listener = response -> {
                System.out.println("Received response like this");
                System.out.println(response.toString());
            };

            Response.ErrorListener errorListener = error -> {
                System.out.println("There is an error while connectiong to the API");
                System.out.println(error.getStackTrace());
            };

            JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.PUT, url, jsonData, listener, errorListener) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>(super.getHeaders());
                    String token = "";
                    try {
                        token = generateXpaytoken();
                        System.out.println("This is my token: " + token);
                    } catch(Exception ex) {
                        System.out.println("Something wrong with an execption");
                        ex.printStackTrace();
                    }

                    headers.put("x-pay-token", token);
                    headers.put("Content-Type", "application/json");
                    headers.put("Accept", "application/json");

                    return headers;
                }
            };

            System.out.println("Headers:\n" + req.getHeaders());
            rq.add(req);
        } catch (Exception ex) {
            System.out.println("Something went wrong");
            ex.printStackTrace();
        }
    }

    private static final String RESOURCE_PATH = "payment/info/" + CALL_ID;
    private static final String QUERY_STRING = "apikey=" + API_KEY;

    public static String generateXpaytoken() throws SignatureException {
        String timestamp = String.valueOf(System.currentTimeMillis()/ 1000L);

        String requestBody = testPayload;
        String beforeHash = timestamp + RESOURCE_PATH + QUERY_STRING + requestBody;
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

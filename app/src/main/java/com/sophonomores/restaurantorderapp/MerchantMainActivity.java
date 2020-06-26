package com.sophonomores.restaurantorderapp;

import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sophonomores.restaurantorderapp.entities.Restaurant;

import org.json.JSONObject;

import java.io.InputStream;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class MerchantMainActivity extends AppCompatActivity implements OrderAdapter.ItemClickListener {

    private static MerchantManager merchantManager;

    private RecyclerView orderRecyclerView;
    private RecyclerView.Adapter orderViewAdapter;
    private RecyclerView.LayoutManager orderLayoutManager;

    public static final String ORDER_INDEX = "ORDER_INDEX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_merchant);

        Restaurant restaurant = new Restaurant("Steak House", "western", null); // hardcoded
        merchantManager = new MerchantManager(restaurant);
        getSupportActionBar().setSubtitle("Confirmed Orders");

        prepareOrderRecyclerView();
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

    // TODO: Change this function into a callback for each new order received
    // TODO: Set request queue as a singleton,
    //  because its lifetime is the same with applicateino ifetime
    public void simulateVppPayment(View view) {
        String url = "https://sandbox.api.visa.com/vdp/helloworld";
        HurlStack hurlStack = new HurlStack(null, generateSSLSocketFactory());
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext(), hurlStack);

        JsonObjectRequest req = new VppRequest(Request.Method.GET, url, null);
        queue.add(req);
    }

    private static final String P12PASSWORD = "GO_BLE_GO";

    private SSLSocketFactory generateSSLSocketFactory() {
        SSLSocketFactory sslSocketFactory;
        KeyStore keyStore;
        X509TrustManager trustManager;

        try {
            keyStore = KeyStore.getInstance("PKCS12");
            InputStream is = getResources().openRawResource(R.raw.restaurant_order_key_and_cert_bundle);
            keyStore.load(is, P12PASSWORD.toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
            kmf.init(keyStore, P12PASSWORD.toCharArray());

            KeyManager[] keyManagers = kmf.getKeyManagers();

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            trustManager = (X509TrustManager) trustManagers[0];

            SSLContext sslContext = SSLContext.getInstance("TLS");

            sslContext.init(keyManagers, new TrustManager[]{trustManager}, null);
            sslSocketFactory = sslContext.getSocketFactory();
            System.out.println("Succesfully create SSL Socket!!!");
        } catch (Exception ex) {
            System.out.println("Something went wrong while generating SSL Socket");
            ex.printStackTrace();
            sslSocketFactory = null;
        }

        return sslSocketFactory;
    }

    // TODO: implement orderactivity
    public void onItemClick(View view, int position) {
//        Intent intent = new Intent(this, OrderActivity.class);
//        intent.putExtra(ORDER_INDEX, position);
//        startActivity(intent);
    }
}

class VppRequest extends JsonObjectRequest {
    private static final String BASIC_AUTH_USERNAME = "Z3TILZ6P9UBKKX51X4GA21c4mjn0tkbcVT5330DsB5Ut8BOXA";
    private static final String BASIC_AUTH_PASSWORD = "J0e4fv8xYKu82Q33Tc7jJUe7wZ86B0Cr";

    public VppRequest(int method, String url, @Nullable JSONObject jsonRequest) {
        super(method, url, jsonRequest, (Response.Listener<JSONObject>) response -> {
            System.out.println("Response received!!!");
            System.out.println(response.toString());
        }, error -> {
            System.out.println("Receive an error code!!");
            System.out.println(error.getCause());
        });
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        String credential = BASIC_AUTH_USERNAME + ":" + BASIC_AUTH_PASSWORD;
        String encodedCredential = Base64.encodeToString(credential.getBytes(), Base64.NO_WRAP);

        Map<String, String> currentHeader = new HashMap<>(super.getHeaders());
        currentHeader.put("Authorization", "Basic " + encodedCredential);
        return currentHeader;
    }
}

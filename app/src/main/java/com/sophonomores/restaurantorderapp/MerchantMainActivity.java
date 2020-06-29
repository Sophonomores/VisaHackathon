package com.sophonomores.restaurantorderapp;

import android.content.Context;
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
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.util.IOUtils;
import com.sophonomores.restaurantorderapp.entities.Restaurant;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPublicKey;
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
        String url = "https://sandbox.api.visa.com/acs/v1/payments/authorizations";
        RequestQueue queue = VppRequestQueue.getInstance(this).getRequestQueue();

        JSONObject payload = null;
        try {
            payload = new JSONObject(getEncryptedPayload());
        } catch (Exception ex) {
            System.out.println("An error occur while converting string to JSON Object");
            ex.printStackTrace();
        }

        JsonObjectRequest req = new VppRequest(Request.Method.POST, url, payload, (Response.Listener<JSONObject>) response -> {
            System.out.println("Response received!!!");
            System.out.println(response.toString());
            System.out.println(decryptResponse(response));
        });

        queue.add(req);
    }

    private static final String dummyPayload = " {" +
            "  \"acctInfo\": {" +
            "    \"primryAcctNum\": {" +
            "      \"pan\": \"4111111111111111\"," +
            "      \"panExpDt\": \"2019-12\"" +
            "    }" +
            "  }," +
            "  \"cardAcceptr\": {" +
            "    \"clientId\": \"0123456789012345678901234567893\"" +
            "  }," +
            "  \"freeFormDescrptnData\": \"Freeformdata\"," +
            "  \"msgIdentfctn\": {" +
            "    \"correlatnId\": \"14bc567d90f23e56a8f045\"," +
            "    \"origId\": \"123451234567890\"" +
            "  }," +
            "  \"msgTransprtData\": \"TransportData\"," +
            "  \"transctn\": {" +
            "    \"eComData\": {" +
            "      \"digitalGoods\": \"true\"," +
            "      \"eciCd\": \"5\"," +
            "      \"xid\": \"EEC4567F90123A5678B0123EA67890D2345678FF\"" +
            "    }," +
            "    \"localDtTm\": \"2020-06-25T16:44:04\"," +
            "    \"partialAuthInd\": \"true\"," +
            "    \"posData\": {" +
            "      \"envrnmnt\": \"eCom\"," +
            "      \"panEntryMode\": \"OnFile\"," +
            "      \"panSrce\": \"VCIND\"" +
            "    }," +
            "    \"tranAmt\": {" +
            "      \"amt\": \"51.29\"," +
            "      \"numCurrCd\": \"840\"" +
            "    }," +
            "    \"tranDesc\": \"Transactiondescription\"" +
            "  }," +
            "  \"verfctnData\": {" +
            "    \"billngAddr\": {" +
            "      \"addrLn\": \"PO Box 12345\"," +
            "      \"postlCd\": \"12345\"" +
            "    }" +
            "  }," +
            "  \"riskAssessmntData\": {" +
            "    \"lowVlExmptn\": \"true\"," +
            "    \"traExmptn\": \"true\"," +
            "    \"trustdMerchntExmptn\": \"true\"," +
            "    \"scpExmptn\": \"true\"," +
            "    \"delgtdAthntctn\": \"true\"" +
            "  }" +
            "}";

    private String getEncryptedPayload() {
        // Define the JWE spec (RSA_OAEP_256 + AES_128_GCM)
        JWEHeader.Builder headerBuilder = new JWEHeader.Builder(
                JWEAlgorithm.RSA_OAEP_256,
                EncryptionMethod.A128GCM);

        // KeyId was taken from VDP board
        headerBuilder.keyID("8a4f15b3-0545-400b-a2f0-6a52c5c88df4");

        // Add token issued at timestamp (iat)
        headerBuilder.customParam("iat", System.currentTimeMillis());

        JWEObject jweObject = new JWEObject(headerBuilder.build(), new Payload(dummyPayload));
        String encryptedPayload = "";

        try {
            String pemEncodedPublicKey = IOUtils.readInputStreamToString(
                    getResources().openRawResource(R.raw.server_cert_for_mle),
                    Charset.forName("UTF-8"));

            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            Certificate cf = certFactory.generateCertificate(
                    new ByteArrayInputStream(pemEncodedPublicKey.getBytes()));

            jweObject.encrypt(new RSAEncrypter((RSAPublicKey) cf.getPublicKey()));
            encryptedPayload = "{\"encData\":\"" + jweObject.serialize() + "\"}";
        } catch(Exception ex ) {
            System.out.println("There is an error while we are encrypting payload!!!");
            ex.printStackTrace();
        }

        return encryptedPayload;
    }

    private String decryptResponse(JSONObject response) {
        String decryptedData = "";

        try {
            JWEObject encryptedData = JWEObject.parse(response.getString("encData"));
            String pemEncodedPrivateKey = IOUtils.readInputStreamToString(
                    getResources().openRawResource(R.raw.client_private_key_cert_for_mle),
                    Charset.forName("UTF-8"));

            JWK jwk = JWK.parseFromPEMEncodedObjects(pemEncodedPrivateKey);

            JWEDecrypter decrypter = new RSADecrypter(jwk.toRSAKey());
            encryptedData.decrypt(decrypter);

            decryptedData = encryptedData.getPayload().toString();
            System.out.println("Here is the real payload: " + decryptedData);
        } catch(Exception ex) {
            System.out.println("There is an issue while decrypting response");
            ex.printStackTrace();
        }

        return decryptedData;
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
    private static final String MLE_KEY_ID = "8a4f15b3-0545-400b-a2f0-6a52c5c88df4";

    public VppRequest(int method, String url, @Nullable JSONObject jsonRequest, Response.Listener<JSONObject> listener) {
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

/**
 * VppRequestQueue acts as a singleton wrapper for RequestQueue.
 * Having a single instance of RequestQueue will be beneficial for merchant app that
 * will make request constantly.
 *
 * Reference: https://developer.android.com/training/volley/requestqueue#singleton
 */
class VppRequestQueue {
    private static VppRequestQueue instance = null;
    private RequestQueue requestQueue;

    private VppRequestQueue(Context context) {
        HurlStack hurlStack = new HurlStack(null, generateSSLSocketFactory(context));
        requestQueue = Volley.newRequestQueue(context.getApplicationContext(), hurlStack);
    }

    public static synchronized VppRequestQueue getInstance(Context context) {
        if (instance == null) {
            instance = new VppRequestQueue(context);
        }

        return instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    /**
     * Export password that were used while combining between certificate and private key.
     */
    private static final String P12PASSWORD = "GO_BLE_GO";

    /**
     * This function simply follow the multi-step protocol to convert our certificate into
     * an SSLSocketFactory. Our certificate here refers to the combination between the project
     * certificate and private key using openssl as mentioned here:
     * https://developer.visa.com/pages/working-with-visa-apis/two-way-ssl
     *
     * @return the SSLSocketFactory which contains the certificate for two-way SSL.
     */
    private static SSLSocketFactory generateSSLSocketFactory(Context context) {
        SSLSocketFactory sslSocketFactory;
        KeyStore keyStore;
        X509TrustManager trustManager;

        try {
            keyStore = KeyStore.getInstance("PKCS12");
            InputStream is = context.getResources()
                    .openRawResource(R.raw.restaurant_order_key_and_cert_bundle);
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
}

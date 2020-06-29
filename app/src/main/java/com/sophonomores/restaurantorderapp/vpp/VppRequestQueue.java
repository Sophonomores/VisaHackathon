package com.sophonomores.restaurantorderapp.vpp;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.sophonomores.restaurantorderapp.R;

import java.io.InputStream;
import java.security.KeyStore;
import java.util.Arrays;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * VppRequestQueue acts as a singleton wrapper for RequestQueue.
 * Having a single instance of RequestQueue will be beneficial for merchant app that
 * will make request constantly.
 *
 * Reference: https://developer.android.com/training/volley/requestqueue#singleton
 */
public class VppRequestQueue {
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

package com.sophonomores.restaurantorderapp.vpp;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
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
import com.sophonomores.restaurantorderapp.R;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPublicKey;
import java.util.function.Consumer;

/**
 * VppConnect provides convenient methods to access available endpoints at VPP. Moreover,
 * VppConnect will automatically use Two-way SSL and MLE to communicate with the API.
 */
public class VppConnect {
    // List of available API endpoints. Currently, access only available to authorization API.
    private static final String AUTHORIZATION_URL = "https://sandbox.api.visa.com/acs/v1/payments/authorizations";

    /**
     * Send payload to the authorization API.
     * Payload should be given as a clean text or unencrypted.
     * VppConnect will handle the encryption and the decryption.
     *
     * @param callback is the function to call after the response has been decrypted
     */
    public static void authorize(String payload, Consumer<String> callback) {
        String url = "https://sandbox.api.visa.com/acs/v1/payments/authorizations";
        RequestQueue queue = VppRequestQueue.getInstance().getRequestQueue();

        JSONObject jsonPayload = null;
        try {
            jsonPayload = new JSONObject(encryptPayload(payload));
        } catch (Exception ex) {
            System.out.println("An error occur while converting string to JSON Object");
            ex.printStackTrace();
        }

        JsonObjectRequest req = new VppRequest(Request.Method.POST, url, jsonPayload,
                (Response.Listener<JSONObject>) response -> {
            System.out.println("Response received!!!");
            System.out.println("Encrypted response: " + response.toString());
            String decryptedResponse = decryptResponse(response);
            System.out.println("Decrypted response: " + decryptedResponse);
            callback.accept(decryptedResponse);
        });

        queue.add(req);
    }

    // Return empty string and some logging in the case of error
    private static String encryptPayload(String payload) {
        // Define the JWE spec (RSA_OAEP_256 + AES_128_GCM)
        JWEHeader.Builder headerBuilder = new JWEHeader.Builder(
                JWEAlgorithm.RSA_OAEP_256,
                EncryptionMethod.A128GCM);

        // KeyId was taken from VDP board
        headerBuilder.keyID("8a4f15b3-0545-400b-a2f0-6a52c5c88df4");

        // Add token issued at timestamp (iat)
        headerBuilder.customParam("iat", System.currentTimeMillis());

        JWEObject jweObject = new JWEObject(headerBuilder.build(), new Payload(payload));
        String encryptedPayload = "";

        try {
            String pemEncodedPublicKey = IOUtils.readInputStreamToString(
                    VppRequestQueue.context.getResources().openRawResource(R.raw.server_cert_for_mle),
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

    // Return empty string and some logging in the case of error
    private static String decryptResponse(JSONObject response) {
        String decryptedData = "";

        try {
            JWEObject encryptedData = JWEObject.parse(response.getString("encData"));
            String pemEncodedPrivateKey = IOUtils.readInputStreamToString(
                    VppRequestQueue.context.getResources().openRawResource(R.raw.client_private_key_cert_for_mle),
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
}

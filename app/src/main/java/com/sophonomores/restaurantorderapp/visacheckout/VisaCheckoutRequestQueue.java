package com.sophonomores.restaurantorderapp.visacheckout;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * VisaCheckoutRequestQueue acts as a singleton wrapper for RequestQueue that communicates
 * with Visa Checkout API.
 */
public class VisaCheckoutRequestQueue {
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

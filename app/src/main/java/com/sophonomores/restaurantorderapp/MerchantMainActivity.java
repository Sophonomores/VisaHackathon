package com.sophonomores.restaurantorderapp;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sophonomores.restaurantorderapp.entities.Restaurant;

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
    public void simulateVppPayment(View view) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.google.com";

        StringRequest req = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("We got a response");
                System.out.println(response.substring(0, 500));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!!!");
            }
        });

        queue.add(req);
    }

    // TODO: implement orderactivity
    public void onItemClick(View view, int position) {
//        Intent intent = new Intent(this, OrderActivity.class);
//        intent.putExtra(ORDER_INDEX, position);
//        startActivity(intent);
    }
}

package com.sophonomores.restaurantorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sophonomores.restaurantorderapp.entities.Restaurant;
import com.sophonomores.restaurantorderapp.services.api.StatusCode;
import com.sophonomores.restaurantorderapp.vpp.VppAuthorizationPayload;
import com.sophonomores.restaurantorderapp.vpp.VppConnect;
import com.sophonomores.restaurantorderapp.vpp.VppRequestQueue;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    // This function is created as a convenient way to test the API
    // TODO: Remove this function.
    public void simulateVppPayment(View view) {
        // This whole process should be done on a different thread
        // since the function callback will always be called on the Main Thread.
        Executors.newCachedThreadPool().submit(() -> {
            VppAuthorizationPayload payload = new VppAuthorizationPayload();
            System.out.println(payload.toString());

            CompletableFuture<String> checkoutResponseFuture = new CompletableFuture<>();

            VppConnect.authorize(payload.toString(),(response)-> {
                System.out.println("Clean response is received: " + response);
                checkoutResponseFuture.complete(StatusCode.OK);
            },(statusCode)-> {
                System.out.println("We received this error status code: " + statusCode);
                checkoutResponseFuture.complete(StatusCode.convert(statusCode));
            });

            String checkoutResponse;
            try {
                System.out.println("I have appeared but waiting!");
                checkoutResponse = checkoutResponseFuture.get(3, TimeUnit.SECONDS);
                System.out.println("Finally it came out!!!");
            } catch (TimeoutException ex) {
                checkoutResponse = StatusCode.REQUEST_TIMEOUT;
            } catch (InterruptedException| ExecutionException | CancellationException ex) {
                checkoutResponse = StatusCode.INTERNAL_SERVER_ERROR;
            }

            System.out.println("Here is the checkout response!!! " + checkoutResponse);
        });
    }
}

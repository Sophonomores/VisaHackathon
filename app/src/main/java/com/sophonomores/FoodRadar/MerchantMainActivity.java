package com.sophonomores.FoodRadar;

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

import com.sophonomores.FoodRadar.entities.Restaurant;
import com.sophonomores.FoodRadar.services.Advertiser;
import com.sophonomores.FoodRadar.visacheckout.VisaCheckoutConnect;
import com.sophonomores.FoodRadar.visacheckout.VisaCheckoutRequestQueue;
import com.sophonomores.FoodRadar.visacheckout.VisaCheckoutUpdatePayload;
import com.sophonomores.FoodRadar.vpp.VppRequestQueue;

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

        // simulateUpdateAPI(null);
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
        VisaCheckoutConnect.updateOrder("7936769825670770202", new VisaCheckoutUpdatePayload(), () -> {
            System.out.println("Update is successful");
        }, (errorCode) -> {
            System.out.println("Received this error status code: " + errorCode);
        });
    }
}

package com.sophonomores.restaurantorderapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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


    // TODO: implement orderactivity
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra(ORDER_INDEX, position);
        startActivity(intent);

    }
}

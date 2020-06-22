package com.sophonomores.restaurantorderapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sophonomores.restaurantorderapp.entities.UserProfile;

public class MainActivity extends AppCompatActivity implements RestaurantAdapter.ItemClickListener {

    private static OrderManager orderManager;

    private RecyclerView restaurantRecyclerView;
    private RecyclerView.Adapter restaurantViewAdapter;
    private RecyclerView.LayoutManager restaurantLayoutManager;

    public static final String RESTAURANT_INDEX = "RESTAURANT_INDEX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserProfile user = new UserProfile("username"); // hardcoded
        orderManager = new OrderManager(user);
        getSupportActionBar().setSubtitle("Eateries near me");

        prepareRestaurantRecyclerView();
    }

    public static OrderManager getOrderManager() {
        return orderManager;
    }

    private void prepareRestaurantRecyclerView() {
        restaurantRecyclerView = (RecyclerView) findViewById(R.id.restaurant_recycler_view);

        // add divider
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(restaurantRecyclerView.getContext(),
                                          LinearLayoutManager.VERTICAL);
        restaurantRecyclerView.addItemDecoration(dividerItemDecoration);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        restaurantRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        restaurantLayoutManager = new LinearLayoutManager(this);
        restaurantRecyclerView.setLayoutManager(restaurantLayoutManager);

        // specify an adapter
        restaurantViewAdapter = new RestaurantAdapter(this, orderManager.getRestaurantList());
        ((RestaurantAdapter) restaurantViewAdapter).setClickListener(this);
        restaurantRecyclerView.setAdapter(restaurantViewAdapter);
    }

    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra(RESTAURANT_INDEX, position);
        startActivity(intent);
    }
}

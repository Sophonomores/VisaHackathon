package com.sophonomores.restaurantorderapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sophonomores.restaurantorderapp.entities.UserProfile;
import com.sophonomores.restaurantorderapp.services.Discoverer;

public class CustomerMainActivity extends AppCompatActivity
        implements RestaurantAdapter.ItemClickListener, OrderManager.RestaurantsChangeListener {

    private OrderManager orderManager;

    private RecyclerView restaurantRecyclerView;
    private RecyclerView.Adapter restaurantViewAdapter;
    private RecyclerView.LayoutManager restaurantLayoutManager;
    private ProgressDialog progressDialog;

    public static final String RESTAURANT_INDEX = "RESTAURANT_INDEX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_customer);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setSubtitle("Eateries near me");

        UserProfile user = new UserProfile("username"); // hardcoded

        if (OrderManager.isInitialised()) {
            orderManager = OrderManager.getInstance();
        } else {
            orderManager = OrderManager.init(user, this);
        }

        prepareRestaurantRecyclerView();

        orderManager.setRestaurantsChangeListener(this);
        orderManager.startSearchingForRestaurants();
        progressDialog = ProgressDialog.show(CustomerMainActivity.this, "", "Loading...", true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_customer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            orderManager.startSearchingForRestaurants();
            progressDialog = ProgressDialog.show(CustomerMainActivity.this, "", "Loading...", true);
        } else if (id == R.id.action_card) {
            Intent intent = new Intent(this, CardActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareRestaurantRecyclerView() {
        restaurantRecyclerView = (RecyclerView) findViewById(R.id.restaurant_recycler_view);

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

    @Override
    public void onRestaurantsChange() {
        progressDialog.dismiss();
        restaurantViewAdapter.notifyDataSetChanged();
    }

}

package com.sophonomores.restaurantorderapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sophonomores.restaurantorderapp.entities.UserProfile;

public class CustomerMainActivity extends AppCompatActivity
        implements RestaurantAdapter.ItemClickListener, OrderManager.RestaurantsChangeListener {

    private OrderManager orderManager;

    private RecyclerView restaurantRecyclerView;
    private RecyclerView.Adapter restaurantViewAdapter;
    private RecyclerView.LayoutManager restaurantLayoutManager;
    private TextView textView6;
    private ProgressDialog progressDialog;
    private boolean isLoading = false;

    public static final String RESTAURANT_INDEX = "RESTAURANT_INDEX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_customer);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setSubtitle("Eateries near me");

        UserProfile user = new UserProfile("John Doe"); // hardcoded

        if (OrderManager.isInitialised()) {
            orderManager = OrderManager.getInstance();
        } else {
            orderManager = OrderManager.init(user, this);
        }

        prepareRestaurantRecyclerView();
        textView6 = (TextView) findViewById(R.id.textView6);
        textView6.setVisibility(orderManager.getRestaurantList().size() == 0 ? View.VISIBLE : View.INVISIBLE);

        orderManager.setRestaurantsChangeListener(this);
        isLoading = true;
        orderManager.startSearchingForRestaurants(() -> {
            showProgressDialog();
        });
//        showProgressDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_customer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_card) {
            Intent intent = new Intent(this, CardActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_past_order) {
            Intent intent = new Intent(this, ConfirmedOrdersActivity.class);
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

    private void showProgressDialog() {
        progressDialog = ProgressDialog.show(CustomerMainActivity.this, "", "Loading...", true);
        new Handler().postDelayed(() -> {
            if (!isLoading) {
                progressDialog.dismiss();
            }
        }, 1000);
    }

    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra(RESTAURANT_INDEX, position);
        startActivity(intent);
    }

    @Override
    public void onRestaurantsChange() {
        if (progressDialog != null)
            progressDialog.dismiss();
        isLoading = false;
        restaurantViewAdapter.notifyDataSetChanged();
        textView6.setVisibility(orderManager.getRestaurantList().size() == 0 ? View.VISIBLE : View.INVISIBLE);
    }

}

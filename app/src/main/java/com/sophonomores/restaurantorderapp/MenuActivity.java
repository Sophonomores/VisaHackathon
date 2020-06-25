package com.sophonomores.restaurantorderapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sophonomores.restaurantorderapp.entities.Dish;
import com.sophonomores.restaurantorderapp.entities.Restaurant;
import com.sophonomores.restaurantorderapp.services.Discoverer;
import com.sophonomores.restaurantorderapp.services.Messenger;
import com.sophonomores.restaurantorderapp.services.api.ResourceURIs;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity implements DishAdapter.ItemClickListener {

    private OrderManager orderManager;
    private List<Dish> dishes;

    private RecyclerView menuRecyclerView;
    private RecyclerView.Adapter menuViewAdapter;
    private RecyclerView.LayoutManager menuLayoutManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //orderManager = CustomerMainActivity.getOrderManager();
        orderManager = OrderManager.getInstance();

        Intent intent = getIntent();
        int restaurantIndex = intent.getIntExtra(CustomerMainActivity.RESTAURANT_INDEX, -1);
        Restaurant restaurant = orderManager.getRestaurantList().get(restaurantIndex);

        dishes = new ArrayList<>();
        getSupportActionBar().setSubtitle("Menu at " + restaurant.getName());

        prepareMenuRecyclerView(dishes);

        getDishes(restaurant);
        progressDialog = ProgressDialog.show(MenuActivity.this, "", "Loading...", true);
    }

    private void prepareMenuRecyclerView(List<Dish> dishes) {
        menuRecyclerView = (RecyclerView) findViewById(R.id.menu_recycler_view);

        // add divider
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(menuRecyclerView.getContext(),
                        LinearLayoutManager.VERTICAL);
        menuRecyclerView.addItemDecoration(dividerItemDecoration);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        menuRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        menuLayoutManager = new LinearLayoutManager(this);
        menuRecyclerView.setLayoutManager(menuLayoutManager);

        // specify an adapter
        menuViewAdapter = new DishAdapter(this, dishes);
        ((DishAdapter) menuViewAdapter).setClickListener(this);
        menuRecyclerView.setAdapter(menuViewAdapter);
    }

    private void getDishes(Restaurant r) {
        Messenger m = new Messenger(MenuActivity.this, Discoverer.DEVICE_NAME);
        m.get(r.getEndpointId(), ResourceURIs.MENU, (String response) -> {
            List<Dish> menu = new Gson().fromJson(response, new TypeToken<ArrayList<Dish>>(){}.getType());
            dishes.clear();
            dishes.addAll(menu);
            progressDialog.dismiss();
            menuViewAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Dish dish = dishes.get(position);
        orderManager.addDishToCart(dish);

        String addCartText = dish.getName() + " has been added to your Shopping Cart!";
        Toast.makeText(this, addCartText, Toast.LENGTH_SHORT).show();
    }

    public void goToCart(View view) {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

}

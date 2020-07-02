package com.sophonomores.FoodRadar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sophonomores.FoodRadar.entities.Dish;
import com.sophonomores.FoodRadar.entities.Restaurant;

import java.util.List;

public class MenuActivity extends AppCompatActivity implements DishAdapterWithQuantity
        .ItemClickListener {

    private OrderManager orderManager;
    private List<Dish> dishes;

    private RecyclerView menuRecyclerView;
    private RecyclerView.Adapter menuViewAdapter;
    private RecyclerView.LayoutManager menuLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        orderManager = OrderManager.getInstance();

        Intent intent = getIntent();
        int restaurantIndex = intent.getIntExtra(CustomerMainActivity.RESTAURANT_INDEX, -1);
        Restaurant restaurant = orderManager.getRestaurantList().get(restaurantIndex);
        orderManager.setCurrentRestaurant(restaurant);
        dishes = restaurant.getMenu();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(restaurant.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        prepareMenuRecyclerView(dishes);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        menuViewAdapter.notifyDataSetChanged();
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
        menuViewAdapter = new DishAdapterWithQuantity(this, dishes, orderManager.getCart());
        ((DishAdapterWithQuantity) menuViewAdapter).setClickListener(this);
        menuRecyclerView.setAdapter(menuViewAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Dish dish = dishes.get(position);
        orderManager.addDishToCart(dish);
        menuViewAdapter.notifyDataSetChanged();

        String addCartText = "Added to cart: " + dish.getName();
        Toast.makeText(this, addCartText, Toast.LENGTH_SHORT).show();
    }

    public void goToCart(View view) {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

}

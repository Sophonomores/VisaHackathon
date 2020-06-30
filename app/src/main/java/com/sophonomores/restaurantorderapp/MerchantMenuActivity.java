package com.sophonomores.restaurantorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.sophonomores.restaurantorderapp.entities.Dish;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MerchantMenuActivity extends AppCompatActivity implements DishAdapter.ItemClickListener {

    private MerchantManager merchantManager;

    private RecyclerView menuRecyclerView;
    private RecyclerView.Adapter menuViewAdapter;
    private RecyclerView.LayoutManager menuLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_menu);

        merchantManager = MerchantManager.getInstance();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(merchantManager.getRestaurant().getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        prepareMenuRecyclerView(merchantManager.getRestaurant().getMenu());
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
        menuViewAdapter = new DishAdapter(this, dishes);
        ((DishAdapter) menuViewAdapter).setClickListener(this);
        menuRecyclerView.setAdapter(menuViewAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Dish dish = merchantManager.getRestaurant().getMenu().get(position);
        if (dish.getAvailability()) {
            merchantManager.pauseDish(dish.getName());
        } else {
            merchantManager.continueDish(dish.getName());
        }
        menuViewAdapter.notifyDataSetChanged();
    }
}

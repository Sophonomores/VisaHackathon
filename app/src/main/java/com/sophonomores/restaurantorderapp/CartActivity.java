package com.sophonomores.restaurantorderapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sophonomores.restaurantorderapp.entities.Dish;
import com.sophonomores.restaurantorderapp.entities.Order;
import com.sophonomores.restaurantorderapp.entities.Restaurant;
import com.sophonomores.restaurantorderapp.entities.ShoppingCart;
import com.sophonomores.restaurantorderapp.entities.UserProfile;
import com.sophonomores.restaurantorderapp.services.Discoverer;
import com.sophonomores.restaurantorderapp.services.Messenger;
import com.sophonomores.restaurantorderapp.services.api.ResourceURIs;

import java.util.List;

public class CartActivity extends AppCompatActivity implements DishAdapter.ItemClickListener {

    private OrderManager orderManager;
    private ShoppingCart cart;

    private RecyclerView dishRecyclerView;
    private RecyclerView.Adapter dishAdapter;
    private RecyclerView.LayoutManager dishLayoutManager;
    private TextView textView;
    private TextView priceTextView;
    private Button checkoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        orderManager = OrderManager.getInstance();

        cart = orderManager.getCart();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("My Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textView = (TextView) findViewById(R.id.textView);
        priceTextView = (TextView) findViewById(R.id.priceTextView);
        checkoutButton = findViewById(R.id.button);
        updateUiComponents();

        prepareDishRecyclerView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            cart.clear();
            updateUiComponents();
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateUiComponents() {
        if (dishAdapter != null)
            dishAdapter.notifyDataSetChanged();
        textView.setVisibility(cart.getCount() == 0 ? View.VISIBLE : View.INVISIBLE);
        priceTextView.setText(String.format("$%.2f", cart.getTotalPrice()));
        checkoutButton.setEnabled(cart.getCount() != 0);
    }

    private void prepareDishRecyclerView() {
        dishRecyclerView = findViewById(R.id.cart_dishes_recycler_view);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this,
                LinearLayoutManager.VERTICAL);
        dishRecyclerView.addItemDecoration(itemDecoration);

        dishRecyclerView.setHasFixedSize(true);

        dishLayoutManager = new LinearLayoutManager(this);
        dishRecyclerView.setLayoutManager(dishLayoutManager);

        List<Dish> dishesInCart = cart.getDishes();
        dishAdapter = new DishAdapter(this, dishesInCart);
        ((DishAdapter) dishAdapter).setClickListener(this);
        dishRecyclerView.setAdapter(dishAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Dish dishRemoved = cart.removeDishAtIndex(position);

        String dishRemovedText = "Removed from cart: " + dishRemoved.getName();
        Toast.makeText(this, dishRemovedText, Toast.LENGTH_SHORT).show();

        updateUiComponents();
    }

    public void goToPayment(View view) {
        // There is a bug with order manager: the cart should only allow orders from one restaurant.
        // This is a hack to get the current restaurant.
        Restaurant currentRestaurant = orderManager.getRestaurantList().get(0);
        Order myOrder = Order.confirmOrder(new UserProfile("John Doe"), currentRestaurant, cart.getDishes());
        new Messenger(CartActivity.this, Discoverer.DEVICE_NAME)
                .post(currentRestaurant.getEndpointId(),
                        ResourceURIs.CHECKOUT,
                        new Gson().toJson(myOrder),
                        System.out::println);
//        Intent intent = new Intent(this, PaymentActivity.class);
//        startActivity(intent);
    }

}

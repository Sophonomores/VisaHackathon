package com.sophonomores.restaurantorderapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.sophonomores.restaurantorderapp.services.Discoverer;
import com.sophonomores.restaurantorderapp.services.Messenger;
import com.sophonomores.restaurantorderapp.services.api.ResourceURIs;
import com.sophonomores.restaurantorderapp.services.api.StatusCode;

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
    private ProgressDialog progressDialog;

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
        Restaurant currentRestaurant = orderManager.getCurrentRestaurant();
        Order myOrder = Order.confirmOrder(orderManager.getUser(), currentRestaurant, cart.getDishes());
        progressDialog = ProgressDialog.show(CartActivity.this, "", "Processing...", true);
        if (RestaurantData.USE_HARDCODED_VALUES) {
            new Handler().postDelayed(() -> {
                handleCheckoutSuccess(myOrder);
            }, 1000);
            return;
        }
        new Messenger(CartActivity.this, Discoverer.DEVICE_NAME)
                .post(currentRestaurant.getEndpointId(),
                        ResourceURIs.CHECKOUT,
                        new Gson().toJson(myOrder),
                        (String response) -> {
                            if(response.equals(StatusCode.OK)) {
                                handleCheckoutSuccess(myOrder);
                            }
                        });
//        Intent intent = new Intent(this, PaymentActivity.class);
//        startActivity(intent);
    }

    private void handleCheckoutSuccess(Order myOrder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
        builder.setMessage("Your order has been placed!\nWe will notify you shortly when your food is ready.")
                .setTitle("Order Placed")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(CartActivity.this, CustomerMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    orderManager.addPastOrder(myOrder);
                    orderManager.clearShoppingCart();
                    startActivity(intent);
                });
        AlertDialog dialog = builder.create();
        if (progressDialog != null)
            progressDialog.dismiss();
        dialog.show();
    }
}

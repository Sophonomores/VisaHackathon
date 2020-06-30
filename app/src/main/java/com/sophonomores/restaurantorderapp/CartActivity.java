package com.sophonomores.restaurantorderapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.sophonomores.restaurantorderapp.biometricauth.BiometricAuth;
import com.sophonomores.restaurantorderapp.entities.Dish;
import com.sophonomores.restaurantorderapp.entities.Order;
import com.sophonomores.restaurantorderapp.entities.Restaurant;
import com.sophonomores.restaurantorderapp.entities.ShoppingCart;
import com.sophonomores.restaurantorderapp.services.Discoverer;
import com.sophonomores.restaurantorderapp.services.Messenger;
import com.sophonomores.restaurantorderapp.services.api.ResourceURIs;
import com.sophonomores.restaurantorderapp.services.api.StatusCode;
import com.visa.checkout.CheckoutButton;
import com.visa.checkout.Environment;
import com.visa.checkout.Profile;
import com.visa.checkout.PurchaseInfo;
import com.visa.checkout.VisaCheckoutSdk;
import com.visa.checkout.VisaPaymentSummary;

import java.math.BigDecimal;
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

    private BiometricAuth biometricAuth;

    private static boolean USE_BIOMETRIC = true;

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

        prepareDishRecyclerView();

        biometricAuth = new BiometricAuth(this);
        biometricAuth.setup(this::goToPayment);

        if (!biometricAuth.canAuthenticate()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
            builder.setMessage(R.string.setup_lock_screen)
                    .setTitle("Error")
                    .setPositiveButton("OK", (dialog, which) -> {});
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        textView = (TextView) findViewById(R.id.textView);
        priceTextView = (TextView) findViewById(R.id.priceTextView);
        checkoutButton = findViewById(R.id.checkout_button);
        updateUiComponents();
        setupCheckoutButton();
    }

    private static final String MERCHANT_API_KEY = "ZS3NZWIM8VE6VRIWDTN021sRrCcEVOgUnbX14E59RK3NWZM8Y";
    private static final String VISA_CHECKOUT_PROFILE = "SYSTEMDEFAULT";

    private void setupCheckoutButton() {
        Profile profile = new Profile.ProfileBuilder(MERCHANT_API_KEY, Environment.SANDBOX)
                .setProfileName(VISA_CHECKOUT_PROFILE)
                .build();

        // TODO: Replace Purchas Info with actual value
        PurchaseInfo purchaseInfo = new PurchaseInfo.PurchaseInfoBuilder(new BigDecimal("10.23"),
                PurchaseInfo.Currency.USD)
                .build();

        CheckoutButton visaCheckoutButton = findViewById(R.id.btn_visa_checkout);

        visaCheckoutButton.init(this, profile, purchaseInfo, new VisaCheckoutSdk.VisaCheckoutResultListener() {
            @Override
            public void onButtonClick(LaunchReadyHandler launchReadyHandler) {
                launchReadyHandler.launch();
            }

            @Override
            public void onResult(VisaPaymentSummary visaPaymentSummary) {
                if (VisaPaymentSummary.PAYMENT_SUCCESS.equalsIgnoreCase(visaPaymentSummary.getStatusName())) {
                    Log.d("AppTag", "Success");
                } else if (VisaPaymentSummary.PAYMENT_CANCEL.equalsIgnoreCase(visaPaymentSummary.getStatusName())) {
                    Log.d("AppTag", "Canceled");
                } else if (VisaPaymentSummary.PAYMENT_ERROR.equalsIgnoreCase(visaPaymentSummary.getStatusName())) {
                    Log.d("AppTag", "Error");
                } else if (VisaPaymentSummary.PAYMENT_FAILURE.equalsIgnoreCase(visaPaymentSummary.getStatusName())) {
                    Log.d("AppTag", "Generic Unknown failure");
                }
            }
        });
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
        checkoutButton.setEnabled(cart.getCount() != 0 && (!USE_BIOMETRIC || biometricAuth.canAuthenticate()));
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

    public void goToPayment() {
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

    public void onCheckoutButtonClick(View view) {
        if (USE_BIOMETRIC)
            biometricAuth.authenticate(String.format("$%.2f", cart.getTotalPrice()), "Visa-1234");
        else
            goToPayment();
    }
}

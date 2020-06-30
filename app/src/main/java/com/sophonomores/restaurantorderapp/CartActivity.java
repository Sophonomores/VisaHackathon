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
    private CheckoutButton visaCheckoutButton;
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
        visaCheckoutButton = findViewById(R.id.btn_visa_checkout);
        updateUiComponents();
    }

    private static final String MERCHANT_API_KEY = "ZS3NZWIM8VE6VRIWDTN021sRrCcEVOgUnbX14E59RK3NWZM8Y";
    private static final String VISA_CHECKOUT_PROFILE = "SYSTEMDEFAULT";

    private void setupVisaCheckoutButton() {
        Profile profile = new Profile.ProfileBuilder(MERCHANT_API_KEY, Environment.SANDBOX)
                .setProfileName(VISA_CHECKOUT_PROFILE)
                .build();

        PurchaseInfo purchaseInfo = new PurchaseInfo.PurchaseInfoBuilder(
                new BigDecimal(cart.getTotalPrice()),
                PurchaseInfo.Currency.USD
        ).build();

        visaCheckoutButton.init(this, profile, purchaseInfo, new VisaCheckoutSdk.VisaCheckoutResultListener() {
            @Override
            public void onButtonClick(LaunchReadyHandler launchReadyHandler) {
                launchReadyHandler.launch();
            }

            // TODO: Adjust onResult Handler: On the merchant side, need a sort of verification.
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
        // Reupdate the purchase amount
        setupVisaCheckoutButton();

        Boolean isCheckoutEnabled = cart.getCount() != 0 && (!USE_BIOMETRIC || biometricAuth.canAuthenticate());
        checkoutButton.setEnabled(isCheckoutEnabled);
        visaCheckoutButton.setEnabled(isCheckoutEnabled);
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
                handleCheckoutSuccess(myOrder, 0);
            }, 1000);
            return;
        }
        new Messenger(CartActivity.this, Discoverer.DEVICE_NAME)
                .post(currentRestaurant.getEndpointId(),
                        ResourceURIs.CHECKOUT,
                        new Gson().toJson(myOrder),
                        (String response) -> {
                            System.out.println("Response: " + response);
                            if(response.equals(StatusCode.PAYMENT_DECLINED)) {
                                handlePaymentDeclined();
                                return;
                            }
                            if(response.equals(StatusCode.INTERNAL_SERVER_ERROR)) {
                                handlePaymentFailure();
                                return;
                            }
                            if(response.contains("Unavailable:")) {
                                handleItemsUnavailable(response);
                                return;
                            }
                            handleCheckoutSuccess(myOrder, Integer.parseInt(response));

                        });
//        Intent intent = new Intent(this, PaymentActivity.class);
//        startActivity(intent);
    }

    private void handleCheckoutSuccess(Order myOrder, int orderId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
        builder.setMessage("Your order has been placed!\nWe will notify you shortly when your food is ready.")
                .setTitle("Order placed")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(CartActivity.this, CustomerMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    myOrder.setId(orderId);
                    orderManager.addPastOrder(myOrder);
                    orderManager.clearShoppingCart();
                    startActivity(intent);
                });
        AlertDialog dialog = builder.create();
        if (progressDialog != null)
            progressDialog.dismiss();
        dialog.show();
    }

    private void handlePaymentDeclined() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
        builder.setMessage("Your payment has been declined. Please try again.")
                .setTitle("Payment declined")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {});
        AlertDialog dialog = builder.create();
        if (progressDialog != null)
            progressDialog.dismiss();
        dialog.show();
    }

    private void handlePaymentFailure() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
        builder.setMessage("Unable to connect to Visa at the moment. Please contact the merchant for more information.")
                .setTitle("Payment failed")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {});
        AlertDialog dialog = builder.create();
        if (progressDialog != null)
            progressDialog.dismiss();
        dialog.show();
    }

    private void handleItemsUnavailable(String response) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
        String message = response.substring(response.indexOf(":") + 1, response.length() - 1);
        builder.setMessage("The following item(s) are not available:\n" + message)
                .setTitle("Items unavailable")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {});
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

package com.sophonomores.restaurantorderapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Log;

import com.sophonomores.restaurantorderapp.biometricauth.FingerprintAuthenticationDialogueFragment;
import com.sophonomores.restaurantorderapp.entities.Dish;
import com.sophonomores.restaurantorderapp.entities.Order;
import com.sophonomores.restaurantorderapp.entities.Restaurant;
import com.sophonomores.restaurantorderapp.entities.ShoppingCart;
import com.sophonomores.restaurantorderapp.services.Discoverer;
import com.sophonomores.restaurantorderapp.services.Messenger;
import com.sophonomores.restaurantorderapp.services.api.ResourceURIs;
import com.sophonomores.restaurantorderapp.services.api.StatusCode;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import static android.security.keystore.KeyProperties.BLOCK_MODE_CBC;
import static android.security.keystore.KeyProperties.ENCRYPTION_PADDING_PKCS7;
import static android.security.keystore.KeyProperties.KEY_ALGORITHM_AES;

public class CartActivity extends AppCompatActivity implements DishAdapter.ItemClickListener,
        FingerprintAuthenticationDialogueFragment.Callback {

    public static final String DEFAULT_KEY_NAME = "default_key";

    private static final String TAG = "CartActivity";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String DIALOG_FRAGMENT_TAG = "my_fragment";

    private OrderManager orderManager;
    private ShoppingCart cart;

    private RecyclerView dishRecyclerView;
    private RecyclerView.Adapter dishAdapter;
    private RecyclerView.LayoutManager dishLayoutManager;
    private TextView textView;
    private TextView priceTextView;
    private Button checkoutButton;
    private ProgressDialog progressDialog;

    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private SharedPreferences sharedPreferences;
    private BiometricPrompt biometricPrompt;

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
        checkoutButton = findViewById(R.id.check_out_button);
        updateUiComponents();

        prepareDishRecyclerView();

        setupKeyStoreAndKeyGenerator();
        Cipher defaultCipher = setupCiphers();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        biometricPrompt = createBiometricPrompt();

        setUpCheckoutButton(defaultCipher);
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

    /**
     * Enables or disables checkout buttons and sets the appropriate click listeners.
     *
     * @param defaultCipher the default cipher, used for the checkout button
     */
    private void setUpCheckoutButton(Cipher defaultCipher) {
        Button checkoutButton = findViewById(R.id.check_out_button);

        if (BiometricManager.from(
                this.getApplication()).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            createKey(DEFAULT_KEY_NAME, true);

            checkoutButton.setEnabled(true);
            checkoutButton.setOnClickListener(new CheckoutButtonClickListener(defaultCipher, DEFAULT_KEY_NAME));
        } else {
            Toast.makeText(this, R.string.setup_lock_screen, Toast.LENGTH_LONG).show();
            checkoutButton.setEnabled(false);
        }
    }

    /**
     * Sets up KeyStore and KeyGenerator
     */
    private void setupKeyStoreAndKeyGenerator() {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to get an instance of KeyStore");
        }

        try {
            keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get an instance of KeyGenerator");
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Sets up default cipher
     */
    private Cipher setupCiphers() {
        Cipher defaultCipher;
        try {
            String cipherString = String.format("%s/%s/%s", KEY_ALGORITHM_AES, BLOCK_MODE_CBC,
                    ENCRYPTION_PADDING_PKCS7);
            defaultCipher = Cipher.getInstance(cipherString);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get an instance of Cipher");
        } catch (Exception e) {
            throw e;
        }

        return defaultCipher;
    }

    /**
     * Initialize the [Cipher] instance with the created key in the [createKey] method.
     *
     * @param keyName the key name to init the cipher
     * @return `true` if initialization succeeded, `false` if the lock screen has been disabled or
     * reset after key generation, or if a fingerprint was enrolled after key generation.
     */
    private boolean initCipher(Cipher cipher, String keyName) {
        try {
            keyStore.load(null);
            cipher.init(Cipher.ENCRYPT_MODE, keyStore.getKey(keyName, null));
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Failed to init Cipher");
        }
    }

    // Show confirmation message.
    private void showConfirmation() {
        findViewById(R.id.confirmation_message).setVisibility(View.VISIBLE);
    }

    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with a fingerprint.
     *
     * @param keyName the name of the key to be created
     * @param invalidatedByBiometricEnrollment if `false` is passed, the created key will not be
     * invalidated even if a new fingerprint is enrolled. The default value is `true` - the key will
     * be invalidated if a new fingerprint is enrolled.
     */
    public void createKey(String keyName, Boolean invalidatedByBiometricEnrollment) {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of enrolled
        // fingerprints has changed.
        try {
            keyStore.load(null);

            int keyProperties = KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT;
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec
                    .Builder(keyName, keyProperties)
                    .setBlockModes(BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(ENCRYPTION_PADDING_PKCS7)
                    .setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);

            keyGenerator.init(builder.build());
            keyGenerator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public BiometricPrompt createBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.d(TAG, errorCode + ": " + errString);
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    loginWithPassword();// Because negative button says use application password
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.d(TAG, "Authentication failed for an unknown reason");
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Log.d(TAG, "Authentication was successful");
                goToPayment();
            }
        };

        biometricPrompt = new BiometricPrompt(this, executor, callback);
        return biometricPrompt;
    }

    private BiometricPrompt.PromptInfo createPromptInfo() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.prompt_info_title))
                .setSubtitle(getString(R.string.prompt_info_subtitle))
                .setDescription(getString(R.string.prompt_info_description))
                .setConfirmationRequired(false)
                .setNegativeButtonText(getString(R.string.prompt_info_use_app_password))
                .build();
        return promptInfo;
    }

    private void loginWithPassword() {
        Log.d(TAG, "Use app password");
        AppCompatDialogFragment fragment = new FingerprintAuthenticationDialogueFragment();
        ((FingerprintAuthenticationDialogueFragment) fragment).setCallback(this);
        fragment.show(getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
    }

    private class CheckoutButtonClickListener implements View.OnClickListener {
        private Cipher cipher;
        private String keyName;

        CheckoutButtonClickListener(Cipher cipher, String keyName) {
            this.cipher = cipher;
            this.keyName = keyName;
        }

        @Override
        public void onClick(View view) {
            findViewById(R.id.confirmation_message).setVisibility(View.GONE);

            BiometricPrompt.PromptInfo promptInfo = createPromptInfo();

            if (initCipher(cipher, keyName)) {
                biometricPrompt.authenticate(promptInfo, new BiometricPrompt.CryptoObject(cipher));
            } else {
                loginWithPassword();
            }
        }
    }

}

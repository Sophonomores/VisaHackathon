package com.sophonomores.restaurantorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CardActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton visaButton;
    private RadioButton offlineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Payment Methods");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        radioGroup = findViewById(R.id.radioGroup);
        visaButton = findViewById(R.id.radioButton);
        offlineButton = findViewById(R.id.radioButton2);
        if (OrderManager.getInstance().getPaymentMode() == OrderManager.USE_OFFLINE_PAYMENT) {
            offlineButton.setChecked(true);
            visaButton.setChecked(false);
        } else {
            offlineButton.setChecked(false);
            visaButton.setChecked(true);
        }
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButton2) {
                OrderManager.getInstance().setPaymentMode(OrderManager.USE_OFFLINE_PAYMENT);
            } else {
                OrderManager.getInstance().setPaymentMode(OrderManager.USE_VISA_CHECKOUT);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_card) {
            onAddCardClicked(null);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onAddCardClicked(View view) {
        Intent intent = new Intent(this, AddCardActivity.class);
        startActivity(intent);
    }
}

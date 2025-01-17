package com.sophonomores.FoodRadar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
    }

    public void checkConfirmedOrders(View view) {
        Intent intent = new Intent(this, ConfirmedOrdersActivity.class);
        startActivity(intent);
    }
}
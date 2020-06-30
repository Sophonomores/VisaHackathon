package com.sophonomores.restaurantorderapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class ConfirmedOrdersActivity extends AppCompatActivity {

    private OrderManager orderManager;

    private RecyclerView orderRecyclerView;
    private RecyclerView.Adapter orderViewAdapter;
    private RecyclerView.LayoutManager orderLayoutManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmed_orders);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("My Orders");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        orderManager = OrderManager.getInstance();

        prepareOrderRecyclerView();

        progressDialog = ProgressDialog.show(ConfirmedOrdersActivity.this, "", "Loading...", true);
        orderManager.refreshOrderStatus(ConfirmedOrdersActivity.this, () -> {
            orderViewAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
        });
        new Handler().postDelayed(() -> {
                progressDialog.dismiss();
            }, 2500);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void prepareOrderRecyclerView() {
        orderRecyclerView = (RecyclerView) findViewById(R.id.order_recycler_view);

        // add divider
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(orderRecyclerView.getContext(),
                        LinearLayoutManager.VERTICAL);
        orderRecyclerView.addItemDecoration(dividerItemDecoration);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        orderRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        orderLayoutManager = new LinearLayoutManager(this);
        orderRecyclerView.setLayoutManager(orderLayoutManager);

        // specify an adapter
        orderViewAdapter = new OrderAdapter(this, orderManager.getPastOrders(), true);
        orderRecyclerView.setAdapter(orderViewAdapter);

        TextView textView5 = findViewById(R.id.textView5);
        textView5.setVisibility(orderManager.getPastOrders().size() == 0 ? View.VISIBLE : View.INVISIBLE);
    }

    public void goToCustomerMainActivity(View view) {
        Intent intent = new Intent(this, CustomerMainActivity.class);
        startActivity(intent);
    }
}
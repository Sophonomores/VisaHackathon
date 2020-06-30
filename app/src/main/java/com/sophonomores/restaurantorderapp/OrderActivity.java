package com.sophonomores.restaurantorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sophonomores.restaurantorderapp.entities.Order;

public class OrderActivity extends AppCompatActivity {
    private MerchantManager merchantManager;
    private Order order;

    private RecyclerView orderDetailRecyclerView;
    private RecyclerView.Adapter orderDetailViewAdapter;
    private RecyclerView.LayoutManager orderDetailLayoutManager;
    private Button button4;
    private Button button5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        merchantManager = MerchantMainActivity.getMerchantManager();
        Intent intent = getIntent();
        int orderIndex = intent.getIntExtra(MerchantMainActivity.ORDER_INDEX, -1);
        order = merchantManager.getOrderList().get(orderIndex);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Order from " + order.getCustomerName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button4.setEnabled(order.getStatus() == Order.CONFIRMED);
        button5.setEnabled(order.getStatus() == Order.READY_TO_SERVE);

        prepareOrderDetailRecyclerView(order);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void prepareOrderDetailRecyclerView(Order order) {
        orderDetailRecyclerView = (RecyclerView) findViewById(R.id.order_detail_recycler_view);

        // add divider
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(orderDetailRecyclerView.getContext(),
                        LinearLayoutManager.VERTICAL);
        orderDetailRecyclerView.addItemDecoration(dividerItemDecoration);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        orderDetailRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        orderDetailLayoutManager = new LinearLayoutManager(this);
        orderDetailRecyclerView.setLayoutManager(orderDetailLayoutManager);

        // specify an adapter
        orderDetailViewAdapter = new OrderDetailAdapter(this, order);
        orderDetailRecyclerView.setAdapter(orderDetailViewAdapter);
    }

    public void updateStatus(View view) {
        merchantManager.markAsReady(order);
        String updateStatusText = "Notification has been sent to customer! ";
        Toast.makeText(this, updateStatusText, Toast.LENGTH_SHORT).show();
        button4.setEnabled(order.getStatus() == Order.CONFIRMED);
        button5.setEnabled(order.getStatus() == Order.READY_TO_SERVE);
    }

    public void updateStatus2(View view) {
        merchantManager.markAsCollected(order);
        button4.setEnabled(order.getStatus() == Order.CONFIRMED);
        button5.setEnabled(order.getStatus() == Order.READY_TO_SERVE);
    }
}

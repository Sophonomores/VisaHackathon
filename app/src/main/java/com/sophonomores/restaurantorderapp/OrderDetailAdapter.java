package com.sophonomores.restaurantorderapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sophonomores.restaurantorderapp.entities.Order;


public class OrderDetailAdapter extends  RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder>{
    private Order mData;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    OrderDetailAdapter(Context context, Order data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public OrderDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.order_detail_recyclerview_row, parent, false);
        return new OrderDetailViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(OrderDetailAdapter.OrderDetailViewHolder holder, int position) {
        String customerName = mData.getCustomerName();
        holder.customerTextView.setText(customerName);

        String orderTime = mData.getOrderTime();
        holder.orderTimeTextView.setText(orderTime);

        String dishList = mData.getDishesString();
        holder.dishListTextView.setText(dishList);

        String totalPrice = "Total: $"+ mData.getTotalPrice();
        holder.totalPriceTextView.setText(totalPrice);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return 1;
    }

    // stores and recycles views as they are scrolled off screen
    public class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        TextView customerTextView;
        TextView orderTimeTextView;
        TextView dishListTextView;
        TextView totalPriceTextView;

        OrderDetailViewHolder(View itemView) {
            super(itemView);
            customerTextView = itemView.findViewById(R.id.user_name);
            orderTimeTextView = itemView.findViewById(R.id.order_time);
            dishListTextView = itemView.findViewById(R.id.dish_list);
            totalPriceTextView = itemView.findViewById(R.id.total_price);
        }
    }
}

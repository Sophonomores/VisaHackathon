package com.sophonomores.restaurantorderapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sophonomores.restaurantorderapp.entities.Order;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private boolean isCustomerSide;

    // data is passed into the constructor
    OrderAdapter(Context context, List<Order> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        isCustomerSide = false;
    }

    // data is passed into the constructor
    OrderAdapter(Context context, List<Order> data, boolean isCustomerSide) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.isCustomerSide = isCustomerSide;
    }

    // inflates the row layout from xml when needed
    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.order_recyclerview_row, parent, false);
        return new OrderViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        Order order = mData.get(position);

        if (!isCustomerSide) {
            String customerName = order.getCustomerName();
            holder.customerTextView.setText(customerName);
        } else {
            String restaurantName = order.getRestaurantName();
            holder.customerTextView.setText(restaurantName);
        }

        String dishList = order.getDishesString();
        holder.dishListTextView.setText(dishList);

        String time = order.getOrderTime();
        holder.timeTextView.setText(time);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView customerTextView;
        TextView dishListTextView;
        TextView timeTextView;

        OrderViewHolder(View itemView) {
            super(itemView);
            customerTextView = itemView.findViewById(R.id.user_name);
            dishListTextView = itemView.findViewById(R.id.dish_list);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Order getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
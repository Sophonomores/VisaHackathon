package com.sophonomores.FoodRadar;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sophonomores.FoodRadar.entities.Order;

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

        switch (order.getStatus()) {
            case Order.CONFIRMED:
                holder.statusTextView.setText("PREPARING");
                holder.statusTextView.setTextColor(Color.parseColor("#F7B600"));
                break;
            case Order.READY_TO_SERVE:
                holder.statusTextView.setText("READY TO COLLECT");
                holder.statusTextView.setTextColor(Color.GREEN);
                break;
            case Order.COLLECTED:
                holder.statusTextView.setText("COMPLETED");
                holder.statusTextView.setTextColor(Color.parseColor("#1A1F71"));
                break;
        }
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
        TextView statusTextView;

        OrderViewHolder(View itemView) {
            super(itemView);
            customerTextView = itemView.findViewById(R.id.user_name);
            dishListTextView = itemView.findViewById(R.id.dish_list);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            statusTextView = itemView.findViewById(R.id.order_status);
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
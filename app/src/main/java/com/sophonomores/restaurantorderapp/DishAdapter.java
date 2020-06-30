package com.sophonomores.restaurantorderapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sophonomores.restaurantorderapp.entities.Dish;

import java.util.List;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.DishViewHolder> {

    private List<Dish> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    DishAdapter(Context context, List<Dish> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public DishViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.menu_recyclerview_row, parent, false);
        return new DishViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(DishViewHolder holder, int position) {
        String dishName = mData.get(position).getName();
        if (mData.get(position).getAvailability()) {
            holder.dishNameTextView.setTextColor(Color.BLACK);
            holder.dishNameTextView.setText(dishName);
        } else {
            holder.dishNameTextView.setTextColor(Color.GRAY);
            holder.dishNameTextView.setText(dishName + " (Unavailable)");
        }

        String dishPrice = String.format("$%.2f", mData.get(position).getPrice());
        holder.dishPriceTextView.setText(dishPrice);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class DishViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView dishNameTextView;
        TextView dishPriceTextView;

        DishViewHolder(View itemView) {
            super(itemView);
            dishNameTextView = itemView.findViewById(R.id.dish_name);
            dishPriceTextView = itemView.findViewById(R.id.dish_price);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Dish getItem(int id) {
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

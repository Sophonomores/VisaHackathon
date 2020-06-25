package com.sophonomores.restaurantorderapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.sophonomores.restaurantorderapp.entities.Dish;
import com.sophonomores.restaurantorderapp.entities.ShoppingCart;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class DishAdapterWithQuantity extends RecyclerView.Adapter<DishAdapterWithQuantity.DishViewHolder> {

    private List<Dish> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ShoppingCart mCart;

    // data is passed into the constructor
    DishAdapterWithQuantity(Context context, List<Dish> data, ShoppingCart cart) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mCart = cart;
    }

    // inflates the row layout from xml when needed
    @Override
    public DishViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.menu_qty_recyclerview_row, parent, false);
        return new DishViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(DishViewHolder holder, int position) {
        String dishName = mData.get(position).getName();
        holder.dishNameTextView.setText(dishName);

        String dishPrice = String.format("$%.2f", mData.get(position).getPrice());
        holder.dishPriceTextView.setText(dishPrice);

        int dishQuantity = mCart.getCountForDish(mData.get(position));
        if (dishQuantity == 0) {
            holder.chip.setVisibility(View.INVISIBLE);
        } else {
            holder.chip.setVisibility(View.VISIBLE);
            holder.chip.setText(String.valueOf(dishQuantity));
        }
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
        Chip chip;

        DishViewHolder(View itemView) {
            super(itemView);
            dishNameTextView = itemView.findViewById(R.id.dish_name);
            dishPriceTextView = itemView.findViewById(R.id.dish_price);
            chip = itemView.findViewById(R.id.chip);
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

package com.sophonomores.FoodRadar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sophonomores.FoodRadar.entities.Restaurant;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private List<Restaurant> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    RestaurantAdapter(Context context, List<Restaurant> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.restaurant_recyclerview_row, parent, false);
        return new RestaurantViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(RestaurantViewHolder holder, int position) {
        Restaurant r = mData.get(position);
        holder.myTextView.setText(r.getName());
        holder.categoryTextView.setText(r.getCategory());
        holder.costTextView.setText(r.getCost() + " - " + r.getAtmosphere());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        TextView categoryTextView;
        TextView costTextView;

        RestaurantViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.restaurant_name);
            categoryTextView = itemView.findViewById(R.id.restaurant_category);
            costTextView = itemView.findViewById(R.id.restaurant_cost);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Restaurant getItem(int id) {
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

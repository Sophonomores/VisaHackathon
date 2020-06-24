package com.sophonomores.restaurantorderapp;

import com.sophonomores.restaurantorderapp.entities.Dish;
import com.sophonomores.restaurantorderapp.entities.Restaurant;
import com.sophonomores.restaurantorderapp.entities.Order;


import java.util.List;


public class MerchantManager {
    private List<Order> orderList;
    private Restaurant restaurant;

    public MerchantManager(Restaurant restaurant) {
        this.orderList = new DataSource().getConfirmedOrder();
        this.restaurant = restaurant;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }


    public void markAsReady(Order order) {
        order.setReadyToServe();
    }

}

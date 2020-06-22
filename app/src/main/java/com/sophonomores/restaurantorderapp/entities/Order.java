package com.sophonomores.restaurantorderapp.entities;

import com.sophonomores.restaurantorderapp.entities.Dish;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private UserProfile customer;
    //private Restaurant restaurant;
    private List<Dish> dishes;
    // private double totalPrice;

    private Order (UserProfile customer, List<Dish> dishes) {
        this.customer = customer;
        //this.restaurant = restaurant;
        this.dishes = dishes;
    }

    public static Order confirmOrder (UserProfile customer, List<Dish> dishes) {
        return new Order(customer, dishes);
    }

    public String getCustomerName() {
        return customer.getUsername();
    }

}

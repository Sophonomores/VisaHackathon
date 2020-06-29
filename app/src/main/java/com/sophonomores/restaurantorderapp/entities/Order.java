package com.sophonomores.restaurantorderapp.entities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {

    public static final int CONFIRMED = 1;
    public static final int READY_TO_SERVE = 2;
    public static final int COLLECTED = 3;

    private UserProfile customer;
    private Restaurant restaurant;
    private List<Dish> dishes;
    private String time;
    private int status;
    // TODO: time of order

    private Order (UserProfile customer, Restaurant restaurant, List<Dish> dishes) {
        this.customer = customer;
        this.restaurant = restaurant;
        this.dishes = dishes;
        this.status = CONFIRMED;
    }

    public static Order confirmOrder (UserProfile customer, Restaurant restaurant, List<Dish> dishes) {
        List<Dish> newDishes = new ArrayList<>();
        newDishes.addAll(dishes);
        Order order = new Order(customer, restaurant, newDishes);
        order.setOrderTime(new SimpleDateFormat("HH:mm").format(new Date()));
        return order;
    }

    public String getCustomerName() {
        return customer.getUsername();
    }

    public String getRestaurantName() {
        return restaurant.getName();
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public String getDishesString() {
        StringBuilder s = new StringBuilder();
        for (Dish dish: dishes) {
            s.append("- ");
            s.append(dish.getName());
            s.append("\n");
        }
        return s.toString();
    }

    public double getTotalPrice() {
        double totalPrice = 0;
        for (Dish dish : dishes) {
            totalPrice += dish.getPrice();
    }
        return totalPrice;
    }

    public void setOrderTime(String time) {
        this.time = time;
    }

    public String getOrderTime() {
        return this.time;
    }

    public void setReadyToServe() {
        this.status = READY_TO_SERVE;
    }

}

package com.sophonomores.restaurantorderapp.entities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class Order {

    public static final int CONFIRMED = 1;
    public static final int READY_TO_SERVE = 2;
    public static final int COLLECTED = 3;

    private UserProfile customer;
    private Restaurant restaurant;
    private List<Dish> dishes;
    private String time;
    private int status;
    private int id;
    // TODO: time of order

    // callId is necessary for Visa Checkout.
    // if callId is not empty, it implies that the order is paid using Visa Checkout and
    // could be identified using this id.
    private String callId;

    private Order (UserProfile customer,
                   Restaurant restaurant,
                   List<Dish> dishes,
                   String callId
    ) {
        this.customer = customer;
        this.restaurant = restaurant;
        this.dishes = dishes;
        this.status = CONFIRMED;
        this.callId = callId;
    }

    public static Order confirmOrder (UserProfile customer,
                                      Restaurant restaurant,
                                      List<Dish> dishes,
                                      String callId
    ) {
        List<Dish> newDishes = new ArrayList<>();
        newDishes.addAll(dishes);
        Order order = new Order(customer, restaurant, newDishes, callId);
        order.setOrderTime(new SimpleDateFormat("HH:mm").format(new Date()));
        return order;
    }

    public static Order confirmOrder (UserProfile customer, Restaurant restaurant, List<Dish> dishes) {
        return confirmOrder(customer, restaurant, dishes, null);
    }

    public String getCustomerName() {
        return customer.getUsername();
    }

    public String getRestaurantName() {
        return restaurant.getName();
    }

    public String getRestaurantId() {
        return restaurant.getEndpointId();
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

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setReadyToServe() {
        this.status = READY_TO_SERVE;
    }

    public void setCollected() {
        this.status = COLLECTED;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCallId() {
        return callId;
    }
}

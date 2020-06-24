package com.sophonomores.restaurantorderapp.entities;

import java.util.List;

public class Order {

    public static final int CONFIRMED = 1;
    public static final int READY_TO_SERVE = 2;
    public static final int COLLECTED = 3;


    private UserProfile customer;
    private Restaurant restaurant;
    private List<Dish> dishes;
    private int status;
    // TODO: time of order

    private Order (UserProfile customer, Restaurant restaurant, List<Dish> dishes) {
        this.customer = customer;
        this.restaurant = restaurant;
        this.dishes = dishes;
        this.status = CONFIRMED;
    }

    public static Order confirmOrder (UserProfile customer, Restaurant restaurant, List<Dish> dishes) {
        return new Order(customer, restaurant, dishes);
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

    // TODO: currently hard-coded. Need to integrate with request message. 
    public String getOrderTime() {
        return "12:01";
    }

    public void setReadyToServe() {
        this.status = READY_TO_SERVE;
    }

}

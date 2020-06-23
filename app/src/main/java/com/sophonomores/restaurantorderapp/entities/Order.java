package com.sophonomores.restaurantorderapp.entities;

import java.util.List;

public class Order {

    private UserProfile customer;
    private Restaurant restaurant;
    private List<Dish> dishes;
    // private double totalPrice;

    private Order (UserProfile customer, Restaurant restaurant, List<Dish> dishes) {
        this.customer = customer;
        this.restaurant = restaurant;
        this.dishes = dishes;
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

}

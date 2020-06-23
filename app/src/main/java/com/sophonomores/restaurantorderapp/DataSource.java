package com.sophonomores.restaurantorderapp;

import com.sophonomores.restaurantorderapp.entities.Dish;
import com.sophonomores.restaurantorderapp.entities.Order;
import com.sophonomores.restaurantorderapp.entities.Restaurant;
import com.sophonomores.restaurantorderapp.entities.UserProfile;

import java.util.ArrayList;
import java.util.List;

public class DataSource {

    public DataSource() {
    }

    public List<Restaurant> getRestaurantData() {
        // TODO: change these hard coded things
        List<Dish> westernDishes = new ArrayList<>();
        westernDishes.add(new Dish("Sirloin", 12.50));
        westernDishes.add(new Dish("Rib eye", 13.50));
        westernDishes.add(new Dish("Angus Beef", 14.50));

        List<Dish> chineseDishes = new ArrayList<>();
        chineseDishes.add(new Dish("Xiao La", 12.50));
        chineseDishes.add(new Dish("Zhong La", 13.50));
        chineseDishes.add(new Dish("Da La", 14.50));

        List<Dish> koreanDishes = new ArrayList<>();
        koreanDishes.add(new Dish("Chicken Ramyun", 12.50));
        koreanDishes.add(new Dish("Sam Gye Tang", 13.50));
        koreanDishes.add(new Dish("Bibimbap", 14.50));

        List<Restaurant> restaurants = new ArrayList<>();
        restaurants.add(new Restaurant("Steak House", "Western", westernDishes));
        restaurants.add(new Restaurant("Mala Hotpot", "Chinese", chineseDishes));
        restaurants.add(new Restaurant("Kimchi Ramyun", "Korean", koreanDishes));

        return restaurants;
    }

    public List<Order> getConfirmedOrder () {
        // TODO: change these hard coded things
        List<Dish> western_one = new ArrayList<>();
        western_one.add(new Dish("Sirloin", 12.50));
        Order order_one = Order.confirmOrder(new UserProfile("dummy"), western_one);

        List<Dish> western_two = new ArrayList<>();
        western_two.add(new Dish("Rib eye", 13.50));
        western_two.add(new Dish("Angus Beef", 14.50));
        Order order_two = Order.confirmOrder(new UserProfile("dummy"), western_two);


        List<Order> orders = new ArrayList<>();
        orders.add(order_one);
        orders.add(order_two);

        return orders;
    }
}

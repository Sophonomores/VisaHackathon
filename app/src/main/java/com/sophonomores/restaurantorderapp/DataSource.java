package com.sophonomores.restaurantorderapp;

import com.sophonomores.restaurantorderapp.entities.Dish;
import com.sophonomores.restaurantorderapp.entities.Order;
import com.sophonomores.restaurantorderapp.entities.Restaurant;
import com.sophonomores.restaurantorderapp.entities.UserProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class supplies OrderManager with a list of restaurants discovered.
 */
public class DataSource {

    private RestaurantsChangeListener listener;

    public DataSource() {
    }

    public void setRestaurantsChangeListener(RestaurantsChangeListener listener) {
        this.listener = listener;
    }

    public void notifyRestaurantsChangeListener(List<Restaurant> restaurants) {
        listener.onRestaurantsChange(restaurants);
    }

    public void getListOfNearbyRestaurants() {
        // TODO: to be implemented
        // getting list of nearby restaurants is expected to be asynchronous, so
        // once a list of restaurants is obtained,
        // call `notifyRestaurantsChangeListener` above.

        // hard coded for now
        try {
            TimeUnit.SECONDS.sleep(2); // simulates searching for devices
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // done with searching
        List<Restaurant> restaurantsFound = getRestaurantData();
        notifyRestaurantsChangeListener(restaurantsFound); // simulates callback
    }

    // TODO: change these hard coded things
    public Restaurant makeSteakHouse() {
        List<Dish> westernDishes = new ArrayList<>();
        westernDishes.add(new Dish("Sirloin", 12.50));
        westernDishes.add(new Dish("Rib eye", 13.50));
        westernDishes.add(new Dish("Angus Beef", 14.50));

        return new Restaurant("Steak House", "Western", westernDishes);
    }

    public Restaurant makeMalaHotpot() {
        List<Dish> chineseDishes = new ArrayList<>();
        chineseDishes.add(new Dish("Xiao La", 12.50));
        chineseDishes.add(new Dish("Zhong La", 13.50));
        chineseDishes.add(new Dish("Da La", 14.50));

        return new Restaurant("Mala Hotpot", "Chinese", chineseDishes);
    }

    public Restaurant makeKimchiRamyun() {
        List<Dish> koreanDishes = new ArrayList<>();
        koreanDishes.add(new Dish("Chicken Ramyun", 12.50));
        koreanDishes.add(new Dish("Sam Gye Tang", 13.50));
        koreanDishes.add(new Dish("Bibimbap", 14.50));

        return new Restaurant("Kimchi Ramyun", "Korean", koreanDishes);
    }

    public List<Restaurant> getRestaurantData() {
        List<Restaurant> restaurants = new ArrayList<>();
        restaurants.add(makeSteakHouse());
        restaurants.add(makeMalaHotpot());
        restaurants.add(makeKimchiRamyun());

        return restaurants;
    }

    public List<Order> getConfirmedOrder () {

        Restaurant steakHouse = makeSteakHouse();
        List<Dish> western_one = new ArrayList<>();
        western_one.add(new Dish("Sirloin", 12.50));
        Order order_one = Order.confirmOrder(new UserProfile("Alice"), steakHouse, western_one);

        List<Dish> western_two = new ArrayList<>();
        western_two.add(new Dish("Rib eye", 13.50));
        western_two.add(new Dish("Angus Beef", 14.50));
        Order order_two = Order.confirmOrder(new UserProfile("Bob"), steakHouse, western_two);


        List<Order> orders = new ArrayList<>();
        orders.add(order_one);
        orders.add(order_two);

        return orders;
    }

    // Classes that want to observe changes in the list of restaurants discovered
    // should implement this interface to get notified.
    public interface RestaurantsChangeListener {
        void onRestaurantsChange(List<Restaurant> restaurants);
    }
}

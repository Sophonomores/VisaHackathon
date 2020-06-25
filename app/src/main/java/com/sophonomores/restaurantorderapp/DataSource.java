package com.sophonomores.restaurantorderapp;

import android.content.Context;
import android.os.Handler;

import com.google.gson.Gson;
import com.sophonomores.restaurantorderapp.entities.Dish;
import com.sophonomores.restaurantorderapp.entities.Order;
import com.sophonomores.restaurantorderapp.entities.Restaurant;
import com.sophonomores.restaurantorderapp.entities.UserProfile;
import com.sophonomores.restaurantorderapp.services.Discoverer;
import com.sophonomores.restaurantorderapp.services.Messenger;
import com.sophonomores.restaurantorderapp.services.api.ResourceURIs;

import java.util.ArrayList;
import java.util.List;

/**
 * This class supplies OrderManager with a list of restaurants discovered.
 */
public class DataSource {

    private Context context;
    private RestaurantsChangeListener listener;
    private Discoverer discoverer;

    public DataSource(Context context) {
        this.context = context;
    }

    public void setRestaurantsChangeListener(RestaurantsChangeListener listener) {
        this.listener = listener;
    }

    public void notifyListenerToAddRestaurant(Restaurant restaurant) {
        listener.onRestaurantAdded(restaurant);
        System.out.println("Restaurant added: " +  restaurant.getName());
    }

    public void getListOfNearbyRestaurants() {
        if (discoverer == null) {
            discoverer = new Discoverer(context);
            discoverer.startDiscovery();
            new Handler().postDelayed(() -> {
                processEndpointIds();
            }, 4000);
        } else {
            processEndpointIds();
        }
    }

    private void processEndpointIds() {
        List<String> endpointIds = discoverer.getDevices();
        System.out.println("end points found: " + endpointIds);
        for (String endpointId : endpointIds) {
            Messenger messenger = new Messenger(context, Discoverer.DEVICE_NAME);
            messenger.get(endpointId, ResourceURIs.INFO, (String response) -> {
                System.out.println("response received: " + response);
                Gson gson = new Gson();
                Restaurant restaurant = gson.fromJson(response, Restaurant.class);
                restaurant.setEndpointId(endpointId);
                notifyListenerToAddRestaurant(restaurant);
            });
        }
    }

    // TODO: change these hard coded things
    public static Restaurant makeSteakHouse() {
        List<Dish> westernDishes = new ArrayList<>();
        westernDishes.add(new Dish("Sirloin", 12.50));
        westernDishes.add(new Dish("Rib eye", 13.50));
        westernDishes.add(new Dish("Angus beef", 14.50));
        return new Restaurant("Steak House", "Western", westernDishes, "$$", "Casual");
    }

//    public Restaurant makeMalaHotpot() {
//        List<Dish> chineseDishes = new ArrayList<>();
//        chineseDishes.add(new Dish("Xiao La", 12.50));
//        chineseDishes.add(new Dish("Zhong La", 13.50));
//        chineseDishes.add(new Dish("Da La", 14.50));
//
//        return new Restaurant("Mala Hotpot", "Chinese", chineseDishes);
//    }
//
//    public Restaurant makeKimchiRamyun() {
//        List<Dish> koreanDishes = new ArrayList<>();
//        koreanDishes.add(new Dish("Chicken Ramyun", 12.50));
//        koreanDishes.add(new Dish("Sam Gye Tang", 13.50));
//        koreanDishes.add(new Dish("Bibimbap", 14.50));
//
//        return new Restaurant("Kimchi Ramyun", "Korean", koreanDishes);
//    }
//
//    public List<Restaurant> getRestaurantData() {
//        List<Restaurant> restaurants = new ArrayList<>();
//        restaurants.add(makeSteakHouse());
//        restaurants.add(makeMalaHotpot());
//        restaurants.add(makeKimchiRamyun());
//
//        return restaurants;
//    }

    public static List<Order> getConfirmedOrder () {

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
        void onRestaurantAdded(Restaurant restaurant);
    }
}

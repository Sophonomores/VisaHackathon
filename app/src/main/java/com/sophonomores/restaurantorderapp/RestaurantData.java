package com.sophonomores.restaurantorderapp;

import android.content.Context;

import com.google.gson.Gson;
import com.sophonomores.restaurantorderapp.entities.Dish;
import com.sophonomores.restaurantorderapp.entities.Restaurant;
import com.sophonomores.restaurantorderapp.services.Discoverer;
import com.sophonomores.restaurantorderapp.services.Messenger;
import com.sophonomores.restaurantorderapp.services.api.ResourceURIs;

import java.util.ArrayList;
import java.util.List;

/**
 * This class supplies OrderManager with a list of restaurants discovered.
 */
public class RestaurantData {

    private Context context;
    private RestaurantsChangeListener listener;
    private Discoverer discoverer;

    public static final boolean USE_HARDCODED_VALUES = true;

    public RestaurantData(Context context) {
        this.context = context;
    }

    public void setRestaurantsChangeListener(RestaurantsChangeListener listener) {
        this.listener = listener;
    }

    public void notifyListenerToAddRestaurant(Restaurant restaurant) {
        listener.onRestaurantAdded(restaurant);
        System.out.println("Restaurant added: " +  restaurant.getName());
    }

    public void notifyListenerToChangeRestaurants(List<Restaurant> restaurants) {
        listener.onRestaurantsChange(restaurants);
    }

    public void getListOfNearbyRestaurants(Runnable r) {
        if (USE_HARDCODED_VALUES) {
            Restaurant restaurant = makeSteakHouse();
            restaurant.setEndpointId("FAKE");
            notifyListenerToAddRestaurant(restaurant);
            return;
        }
        if (discoverer == null) {
            discoverer = new Discoverer(context);
            discoverer.startDiscovery((endpointId) -> {
                r.run();
                Messenger messenger = new Messenger(context, Discoverer.DEVICE_NAME);
                messenger.get(endpointId, ResourceURIs.INFO, (String response) -> {
                    System.out.println("response received: " + response);
                    Gson gson = new Gson();
                    Restaurant restaurant = gson.fromJson(response, Restaurant.class);
                    restaurant.setEndpointId(endpointId);
                    notifyListenerToAddRestaurant(restaurant);
                });
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

    // Classes that want to observe changes in the list of restaurants discovered
    // should implement this interface to get notified.
    public interface RestaurantsChangeListener {
        void onRestaurantsChange(List<Restaurant> restaurants);
        void onRestaurantAdded(Restaurant restaurant);
    }
}

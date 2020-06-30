package com.sophonomores.restaurantorderapp;

import android.content.Context;

import com.google.gson.Gson;
import com.sophonomores.restaurantorderapp.entities.Dish;
import com.sophonomores.restaurantorderapp.entities.Order;
import com.sophonomores.restaurantorderapp.entities.Restaurant;
import com.sophonomores.restaurantorderapp.entities.ShoppingCart;
import com.sophonomores.restaurantorderapp.entities.UserProfile;
import com.sophonomores.restaurantorderapp.services.Discoverer;
import com.sophonomores.restaurantorderapp.services.Messenger;
import com.sophonomores.restaurantorderapp.services.api.ResourceURIs;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the main business logic of the customer app.
 * View (various Activity classes) should access the app through this class.
 */
public class OrderManager implements RestaurantData.RestaurantsChangeListener {

    // enforce Singleton pattern
    private static OrderManager instance;

    private List<Restaurant> restaurantList;
    private Restaurant currentRestaurant;
    private UserProfile user;
    private ShoppingCart cart;
    private RestaurantData restaurantData;
    private List<Order> pastOrders;

    // register observer
    private RestaurantsChangeListener listener;

    private OrderManager(Context context) {
        this.restaurantData = new RestaurantData(context);
        this.restaurantList = new ArrayList<>();
        this.user = null;
        this.cart = new ShoppingCart();
        this.pastOrders = new ArrayList<>();
    }

    public void startSearchingForRestaurants(Runnable r) {
        restaurantData.setRestaurantsChangeListener(this);
        restaurantData.getListOfNearbyRestaurants(r);
    }

    public static OrderManager init(UserProfile user, Context context) {
        instance = new OrderManager(context);
        instance.user = user;
        return instance;
    }

    public static OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager(null); // null for now
        }

        return instance;
    }

    public static boolean isInitialised() {
        return instance != null;
    }

    public List<Restaurant> getRestaurantList() {
        return restaurantList;
    }

    public void setRestaurantList(List<Restaurant> restaurantList) {
        if (this.restaurantList == null) {
            this.restaurantList = new ArrayList<>();
        }
        // to keep any existing references to this list referring to the correct list
        this.restaurantList.clear();
        this.restaurantList.addAll(restaurantList);
    }

    public UserProfile getUser() {
        return user;
    }

    public ShoppingCart getCart() {
        return cart;
    }

    public void addDishToCart(Dish dish) {
        cart.addDish(dish);
    }

    public void removeDishFromCart(Dish dish) {
        cart.removeDish(dish);
    }

    public void clearShoppingCart() {
        cart.clear();
    }

    public double getCartTotalPrice() {
        return cart.getTotalPrice();
    }

    public Restaurant getCurrentRestaurant() {
        return currentRestaurant;
    }

    public void setCurrentRestaurant(Restaurant r) {
        currentRestaurant = r;
    }

    public List<Order> getPastOrders() {
        return this.pastOrders;
    }

    public void addPastOrder(Order o) {
        this.pastOrders.add(o);
    }

    public void setRestaurantsChangeListener(RestaurantsChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onRestaurantsChange(List<Restaurant> restaurants) {
        System.out.println("setting restaurant list...");
        setRestaurantList(restaurants);
        // update UI that observes this OrderManager class
        listener.onRestaurantsChange();
    }

    @Override
    public void onRestaurantAdded(Restaurant restaurant) {
        if (!restaurantList.contains(restaurant)) {
            restaurantList.add(restaurant);
        }
        // update UI that observes this OrderManager class
        listener.onRestaurantsChange();
    }

    // This interface is to register UI to observer changes in the list of restaurants in
    // OrderManager class.
    public interface RestaurantsChangeListener {
        void onRestaurantsChange();
    }

    public void refreshOrderStatus(Context c, Runnable r) {
        for (Order o : pastOrders) {
            if (o.getStatus() == Order.CONFIRMED) {
                new Messenger(c, Discoverer.DEVICE_NAME)
                        .post(o.getRestaurantId(),
                                ResourceURIs.STATUS,
                                String.valueOf(o.getId()),
                                (String response) -> {
                                    o.setStatus(Integer.parseInt(response));
                                    r.run();
                                });
            }
        }
    }
}

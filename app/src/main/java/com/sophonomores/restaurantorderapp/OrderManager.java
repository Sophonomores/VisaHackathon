package com.sophonomores.restaurantorderapp;

import android.content.Context;

import com.sophonomores.restaurantorderapp.entities.Dish;
import com.sophonomores.restaurantorderapp.entities.Restaurant;
import com.sophonomores.restaurantorderapp.entities.ShoppingCart;
import com.sophonomores.restaurantorderapp.entities.UserProfile;

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
    private UserProfile user;
    private ShoppingCart cart;
    private RestaurantData restaurantData;

    // register observer
    private RestaurantsChangeListener listener;

    private OrderManager(Context context) {
        this.restaurantData = new RestaurantData(context);
        this.restaurantList = new ArrayList<>();
        this.user = null;
        this.cart = new ShoppingCart();
    }

    public void startSearchingForRestaurants() {
        restaurantData.setRestaurantsChangeListener(this);
        restaurantData.getListOfNearbyRestaurants();
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

    public void setRestaurantsChangeListener(RestaurantsChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onRestaurantsChange(List<Restaurant> restaurants) {
        setRestaurantList(restaurants);
        // update UI that observes this OrderManager class
        listener.onRestaurantsChange();
    }

    @Override
    public void onRestaurantAdded(Restaurant restaurant) {
        restaurantList.add(restaurant);
        // update UI that observes this OrderManager class
        listener.onRestaurantsChange();
    }

    // This interface is to register UI to observer changes in the list of restaurants in
    // OrderManager class.
    public interface RestaurantsChangeListener {
        void onRestaurantsChange();
    }
}

package com.sophonomores.restaurantorderapp;

import com.sophonomores.restaurantorderapp.entities.Dish;
import com.sophonomores.restaurantorderapp.entities.Restaurant;
import com.sophonomores.restaurantorderapp.entities.ShoppingCart;
import com.sophonomores.restaurantorderapp.entities.UserProfile;

import java.util.List;

/**
 * This class handles the main business logic of the customer app.
 * View (various Activity classes) should access the app through this class.
 */
public class OrderManager {

    // enforce Singleton pattern
    private static OrderManager instance;

    private List<Restaurant> restaurantList;
    private UserProfile user;
    private ShoppingCart cart;

    private OrderManager() {
        this.restaurantList = new DataSource().getRestaurantData();
        this.user = null;
        this.cart = new ShoppingCart();
    }

    public static OrderManager init(UserProfile user) {
        instance = new OrderManager();
        instance.user = user;
        return instance;
    }

    public static OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }

        return instance;
    }

    public static boolean isInitialised() {
        return instance != null;
    }

    public List<Restaurant> getRestaurantList() {
        return restaurantList;
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
}

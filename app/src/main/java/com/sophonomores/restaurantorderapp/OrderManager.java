package com.sophonomores.restaurantorderapp;

import com.sophonomores.restaurantorderapp.entities.Dish;
import com.sophonomores.restaurantorderapp.entities.Restaurant;
import com.sophonomores.restaurantorderapp.entities.ShoppingCart;
import com.sophonomores.restaurantorderapp.entities.UserProfile;

import java.util.List;

public class OrderManager {

    private List<Restaurant> restaurantList;
    private UserProfile user;
    private ShoppingCart cart;

    public OrderManager(UserProfile user) {
        this.restaurantList = new DataSource().getRestaurantData();
        this.user = user;
        this.cart = new ShoppingCart();
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
}

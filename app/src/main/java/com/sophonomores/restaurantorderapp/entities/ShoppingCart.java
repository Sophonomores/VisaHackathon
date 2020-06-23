package com.sophonomores.restaurantorderapp.entities;

import com.sophonomores.restaurantorderapp.entities.Dish;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private List<Dish> dishes;

    public ShoppingCart() {
        this.dishes = new ArrayList<>();
    }

    public ShoppingCart(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void addDish(Dish dish) {
        dishes.add(dish);
    }

    public void removeDish(Dish dish) {
        dishes.remove(dish);
    }

    public Dish removeDishAtIndex(int index) {
        return dishes.remove(index);
    }
}

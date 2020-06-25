package com.sophonomores.restaurantorderapp.entities;

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

    public void clear() {
        dishes = new ArrayList<>();
    }

    public Double getTotalPrice() {
        double total = 0;
        for (Dish dish : dishes) {
            total += dish.getPrice();
        }
        return total;
    }

    public int getCount() {
        return dishes.size();
    }

    // for testing
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Dish dish : dishes) {
            sb.append(dish.getName() + "\n");
        }

        return sb.toString();
    }
}

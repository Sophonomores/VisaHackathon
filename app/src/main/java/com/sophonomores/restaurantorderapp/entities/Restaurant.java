package com.sophonomores.restaurantorderapp.entities;

import com.sophonomores.restaurantorderapp.entities.Dish;

import java.util.List;

public class Restaurant {

    private String name;
    private String category;
    private List<Dish> dishes;

    public Restaurant(String name, String category, List<Dish> dishes) {
        this.name = name;
        this.category = category;
        this.dishes = dishes;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public List<Dish> getDishes() {
        return dishes;
    }
}

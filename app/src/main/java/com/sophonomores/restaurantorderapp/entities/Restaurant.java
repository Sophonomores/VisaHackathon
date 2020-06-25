package com.sophonomores.restaurantorderapp.entities;

import java.util.List;

public class Restaurant {

    private String name;
    private String category;
    private List<Dish> menu;
    private String endpointId;

    public Restaurant(String name, String category, List<Dish> menu) {
        this.name = name;
        this.category = category;
        this.menu = menu;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public List<Dish> getMenu() {
        return menu;
    }

    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }

    public String getEndpointId() {
        return endpointId;
    }
}

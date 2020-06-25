package com.sophonomores.restaurantorderapp.entities;

import com.sophonomores.restaurantorderapp.entities.Dish;

import java.util.List;

public class Restaurant {

    private String name;
    private String category;
    private String endpointId;

    public Restaurant(String name, String category) {
        this.name = name;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }

    public String getEndpointId() {
        return endpointId;
    }
}

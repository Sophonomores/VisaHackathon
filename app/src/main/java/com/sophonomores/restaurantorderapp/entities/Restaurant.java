package com.sophonomores.restaurantorderapp.entities;

import java.util.List;

public class Restaurant {

    private String name;
    private String category;
    private List<Dish> menu;
    private String cost;
    private String atmosphere;
    private String endpointId;

    public Restaurant(String name, String category, List<Dish> menu, String cost, String atmosphere) {
        this.name = name;
        this.category = category;
        this.menu = menu;
        this.cost = cost;
        this.atmosphere = atmosphere;
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

    public String getCost() {
        return cost;
    }

    public String getAtmosphere() {
        return atmosphere;
    }

    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }

    public String getEndpointId() {
        return endpointId;
    }

    // simplified version
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Restaurant)) {
            return false;
        }
        Restaurant r = (Restaurant) o;
        return r.getEndpointId().equals(endpointId);
    }
}

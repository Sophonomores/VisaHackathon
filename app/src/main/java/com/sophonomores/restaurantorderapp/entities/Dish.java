package com.sophonomores.restaurantorderapp.entities;

public class Dish {

    private String name;
    private double price;

    public Dish(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Dish)) {
            return false;
        }
        Dish d = (Dish) o;
        return d.getName().equals(name);
    }
}

package com.sophonomores.FoodRadar.entities;

public class Dish {

    private String name;
    private double price;
    private boolean isAvailable;

    public Dish(String name, double price) {
        this.name = name;
        this.price = price;
        isAvailable = true;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public boolean getAvailability() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
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

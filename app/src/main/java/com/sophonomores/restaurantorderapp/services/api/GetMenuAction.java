package com.sophonomores.restaurantorderapp.services.api;

import android.util.JsonWriter;

import com.sophonomores.restaurantorderapp.entities.Dish;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class GetMenuAction extends Action {
    @Override
    public String execute(@Nullable String input) {
        try {
            StringWriter sw = new StringWriter();
            JsonWriter jw = new JsonWriter(sw);
            jw.beginArray();
            for (Dish d : getSteakHouseMenu()) {
                jw.beginObject();
                jw.name("name").value(d.getName());
                jw.name("price").value(d.getPrice());
                jw.endObject();
            }
            jw.endArray();
            return sw.toString();
        } catch (IOException e) {
            return StatusCode.INTERNAL_SERVER_ERROR;
        }
    }

    private List<Dish> getSteakHouseMenu() {
        List<Dish> westernDishes = new ArrayList<>();
        westernDishes.add(new Dish("Sirloin", 12.50));
        westernDishes.add(new Dish("Rib eye", 13.50));
        westernDishes.add(new Dish("Angus Beef", 14.50));
        return westernDishes;
    }
}

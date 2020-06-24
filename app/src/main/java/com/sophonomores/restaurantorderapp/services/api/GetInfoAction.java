package com.sophonomores.restaurantorderapp.services.api;

import android.util.JsonWriter;

import com.sophonomores.restaurantorderapp.entities.Dish;
import com.sophonomores.restaurantorderapp.entities.Restaurant;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class GetInfoAction extends Action {
    @Override
    public String execute(@Nullable String input) {
        try {
            StringWriter sw = new StringWriter();
            JsonWriter jw = new JsonWriter(sw);
            jw.beginObject();
            jw.name("name").value("Steak House");
            jw.name("category").value("Western");
            jw.endObject();
            return sw.toString();
        } catch (IOException e) {
            return StatusCode.INTERNAL_SERVER_ERROR;
        }
    }
}

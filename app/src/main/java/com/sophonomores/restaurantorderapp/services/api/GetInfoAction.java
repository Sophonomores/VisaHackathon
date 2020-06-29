package com.sophonomores.restaurantorderapp.services.api;

import com.google.gson.Gson;
import com.sophonomores.restaurantorderapp.RestaurantData;

import androidx.annotation.Nullable;

public class GetInfoAction extends Action {
    @Override
    public String execute(@Nullable String input) {
        return new Gson().toJson(RestaurantData.makeSteakHouse());
    }
}

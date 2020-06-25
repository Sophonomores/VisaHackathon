package com.sophonomores.restaurantorderapp.services.api;

import com.google.gson.Gson;
import com.sophonomores.restaurantorderapp.DataSource;

import androidx.annotation.Nullable;

public class GetMenuAction extends Action {
    @Override
    public String execute(@Nullable String input) {
        return new Gson().toJson(DataSource.makeWesternMenu());
    }
}

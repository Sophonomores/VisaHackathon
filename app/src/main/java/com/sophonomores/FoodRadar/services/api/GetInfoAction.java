package com.sophonomores.FoodRadar.services.api;

import android.content.Context;

import com.google.gson.Gson;
import com.sophonomores.FoodRadar.RestaurantData;

import java.util.function.Consumer;

import androidx.annotation.Nullable;

public class GetInfoAction extends Action {
    @Override
    public void execute(@Nullable String input, Context context, Consumer<String> consumer) {
        consumer.accept(new Gson().toJson(RestaurantData.makeSteakHouse()));
    }
}

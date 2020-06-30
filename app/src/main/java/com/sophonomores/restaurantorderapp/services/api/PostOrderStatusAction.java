package com.sophonomores.restaurantorderapp.services.api;

import android.content.Context;

import com.google.gson.Gson;
import com.sophonomores.restaurantorderapp.MerchantManager;
import com.sophonomores.restaurantorderapp.RestaurantData;

import java.util.function.Consumer;

import androidx.annotation.Nullable;

public class PostOrderStatusAction extends Action {
    @Override
    public void execute(@Nullable String input, Context context, Consumer<String> consumer) {
        consumer.accept(String.valueOf(MerchantManager.getInstance().getOrderStatus(Integer.parseInt(input))));
    }
}

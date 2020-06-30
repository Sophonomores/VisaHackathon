package com.sophonomores.restaurantorderapp.services.api;

import com.google.gson.Gson;
import com.sophonomores.restaurantorderapp.MerchantManager;
import com.sophonomores.restaurantorderapp.RestaurantData;

import androidx.annotation.Nullable;

public class PostOrderStatusAction extends Action {
    @Override
    public String execute(@Nullable String input) {
        return String.valueOf(MerchantManager.getInstance().getOrderStatus(Integer.parseInt(input)));
    }
}

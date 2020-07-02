package com.sophonomores.FoodRadar.services.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sophonomores.FoodRadar.MerchantManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import androidx.annotation.Nullable;

public class PostOrderStatusAction extends Action {
    @Override
    public void execute(@Nullable String input, Context context, Consumer<String> consumer) {
        List<Integer> inputIds = new Gson().fromJson(input, new TypeToken<ArrayList<Integer>>(){}.getType());
        MerchantManager manager = MerchantManager.getInstance();
        List<Integer> output = inputIds.stream().map(manager::getOrderStatus).collect(Collectors.toList());
        consumer.accept(new Gson().toJson(output));
    }
}

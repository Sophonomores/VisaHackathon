package com.sophonomores.restaurantorderapp.services.api;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.sophonomores.restaurantorderapp.OrderData;
import com.sophonomores.restaurantorderapp.RestaurantData;
import com.sophonomores.restaurantorderapp.entities.Dish;
import com.sophonomores.restaurantorderapp.entities.Order;
import com.sophonomores.restaurantorderapp.entities.Restaurant;
import com.sophonomores.restaurantorderapp.entities.UserProfile;
import com.sophonomores.restaurantorderapp.vpp.VppConnect;

import java.util.ArrayList;
import java.util.List;


public class PostCheckoutAction extends Action {
    @Override
    public String execute(@Nullable String input) {
        // TODO: Change dummyPayload with actual input
        // TODO: Modify the response callback
        VppConnect.authorize(VppConnect.dummyPayload, (response) -> {
            System.out.println("Clean response is received: " + response);
        });

        OrderData.notifyListenerToAddOrder(new Gson().fromJson(input, Order.class));
        return StatusCode.OK;
    }

    public void sendToOrderData (String input) {

        Restaurant steakHouse =  RestaurantData.makeSteakHouse();
        List<Dish> western_one = new ArrayList<>();
        western_one.add(new Dish("Sirloin", 12.50));
        Order order = Order.confirmOrder(new UserProfile("Alice"), steakHouse, western_one);
        OrderData.notifyListenerToAddOrder(order);
    }
}

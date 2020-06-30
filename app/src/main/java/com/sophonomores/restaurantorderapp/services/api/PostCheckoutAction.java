package com.sophonomores.restaurantorderapp.services.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.sophonomores.restaurantorderapp.OrderData;
import com.sophonomores.restaurantorderapp.RestaurantData;
import com.sophonomores.restaurantorderapp.entities.Dish;
import com.sophonomores.restaurantorderapp.entities.Order;
import com.sophonomores.restaurantorderapp.entities.Restaurant;
import com.sophonomores.restaurantorderapp.entities.UserProfile;
import com.sophonomores.restaurantorderapp.vpp.VppAuthorizationPayload;
import com.sophonomores.restaurantorderapp.vpp.VppConnect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class PostCheckoutAction extends Action {
    @Override
    public void execute(@Nullable String input, Context context, Consumer<String> consumer) {
        // TODO: Insert PAN into the payload
        // TODO: Modify the response callback
        Order order = new Gson().fromJson(input, Order.class);

        VppAuthorizationPayload payload = new VppAuthorizationPayload();
        if (order.getTotalPrice() == 11.11)
            payload.transactionAmount = order.getTotalPrice();

        ProgressDialog pd = ProgressDialog.show(context, "", "Processing payment...", true, false);
        System.out.println("Processing payment...");
        VppConnect.authorize(payload.toString(), (response) -> {
            System.out.println("Clean response is received: " + response);
            OrderData.notifyListenerToAddOrder(order);
            pd.dismiss();
            Toast.makeText(context, "Payment approved", Toast.LENGTH_SHORT).show();
            consumer.accept(String.valueOf(OrderData.notifyListenerToAddOrder(new Gson().fromJson(input, Order.class))));
        }, (statusCode) -> {
            System.out.println("We received this error status code: " + statusCode);
            pd.dismiss();
            Toast.makeText(context, "Payment declined", Toast.LENGTH_SHORT).show();
            consumer.accept(StatusCode.convert(statusCode));
        });
    }

    public void sendToOrderData (String input) {

        Restaurant steakHouse =  RestaurantData.makeSteakHouse();
        List<Dish> western_one = new ArrayList<>();
        western_one.add(new Dish("Sirloin", 12.50));
        Order order = Order.confirmOrder(new UserProfile("Alice"), steakHouse, western_one);
        OrderData.notifyListenerToAddOrder(order);
    }
}

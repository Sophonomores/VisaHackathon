package com.sophonomores.restaurantorderapp.services.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.sophonomores.restaurantorderapp.MerchantManager;
import com.sophonomores.restaurantorderapp.OrderData;
import com.sophonomores.restaurantorderapp.RestaurantData;
import com.sophonomores.restaurantorderapp.entities.Dish;
import com.sophonomores.restaurantorderapp.entities.Order;
import com.sophonomores.restaurantorderapp.entities.Restaurant;
import com.sophonomores.restaurantorderapp.entities.UserProfile;
import com.sophonomores.restaurantorderapp.visacheckout.VisaCheckoutConnect;
import com.sophonomores.restaurantorderapp.visacheckout.VisaCheckoutUpdatePayload;
import com.sophonomores.restaurantorderapp.vpp.VppAuthorizationPayload;
import com.sophonomores.restaurantorderapp.vpp.VppConnect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class PostCheckoutAction extends Action {
    @Override
    public void execute(@Nullable String input, Context context, Consumer<String> consumer) {
        // TODO: Insert PAN into the payload
        Order order = new Gson().fromJson(input, Order.class);
        MerchantManager manager = MerchantManager.getInstance();
        List<Dish> unavailableItems = manager.checkOrderAvailability(order);
        if (unavailableItems.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Unavailable:");
            for(Dish d : unavailableItems) {
                sb.append(d.getName() + ",");
            }
            consumer.accept(sb.toString());
            return;
        }

        String message = "Processing payment...";
        if (order.getCallId() != null) {
            message = "Contacting Visa Checkout...";
        } else {
            message = "Contacting Visa Payments Processing...";
        }

        ProgressDialog pd = ProgressDialog.show(context, "", message, true, false);

        Runnable onSuccess = () -> {
            pd.dismiss();
            Toast.makeText(context, "Payment approved", Toast.LENGTH_SHORT).show();
            consumer.accept(String.valueOf(OrderData.notifyListenerToAddOrder(order)));
        };

        Consumer<Integer> onFailure = (statusCode) -> {
            pd.dismiss();
            Toast.makeText(context, "Payment failed", Toast.LENGTH_SHORT).show();
            consumer.accept(StatusCode.convert(statusCode));
        };

        // If callId is available, it implies the use of Visa Checkout API.
        if (order.getCallId() != null) {
            String callId = order.getCallId();
            proceedWithVisaCheckout(callId, order, context, consumer, onSuccess, onFailure);
        } else {
            proceedWithVppAPI(order, context, consumer, onSuccess, onFailure);
        }
    }

    private void proceedWithVppAPI(Order order,
                                   Context context,
                                   Consumer<String> consumer,
                                   Runnable onSuccess,
                                   Consumer<Integer> onFailure
    ) {
        VppAuthorizationPayload payload = new VppAuthorizationPayload();

        // To demonstrate a decline payment
        if (order.getTotalPrice() == 11.11)
            payload.transactionAmount = order.getTotalPrice();

        System.out.println("Processing payment with VPP...");
        VppConnect.authorize(payload.toString(), (response) -> {
            System.out.println("Clean response is received: " + response);
            onSuccess.run();
        }, (statusCode) -> {
            System.out.println("We received this error status code: " + statusCode);
            onFailure.accept(statusCode);
        });
    }

    private void proceedWithVisaCheckout(
            String callId,
            Order order,
            Context context,
            Consumer<String> consumer,
            Runnable onSuccess,
            Consumer<Integer> onFailure
    ) {
        VisaCheckoutUpdatePayload payload = new VisaCheckoutUpdatePayload();
        payload.total = order.getTotalPrice();
        payload.eventType = VisaCheckoutUpdatePayload.EventType.confirm;

        System.out.println("Processing payment with Visa Checkout...");
        VisaCheckoutConnect.updateOrder(callId, payload, () -> {
            System.out.println("Order confirmation is successful");
            onSuccess.run();
        }, (statusCode) -> {
            System.out.println("Received this error status code: " + statusCode);
            onFailure.accept(statusCode);
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

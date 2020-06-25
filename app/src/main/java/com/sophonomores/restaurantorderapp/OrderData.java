package com.sophonomores.restaurantorderapp;

import android.content.Context;

import com.sophonomores.restaurantorderapp.entities.Dish;
import com.sophonomores.restaurantorderapp.entities.Order;
import com.sophonomores.restaurantorderapp.entities.Restaurant;
import com.sophonomores.restaurantorderapp.entities.UserProfile;
import com.sophonomores.restaurantorderapp.services.Messenger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderData {

    private Context context;
    private OrderListener listener;

    public OrderData(Context context) {
        this.context = context;
    }

    public void setOrderListener(OrderListener listener) {
        this.listener = listener;
    }

    public void notifyListenerToAddOrder(Order order) {
        listener.onNewOrder(order);
        System.out.println("New order coming from: " +  order.getCustomerName());
    }

//    public void getListOfNearbyOrders() {
//
//        Discoverer discoverer = new Discoverer(context);
//        discoverer.startDiscovery();
//        List<String> endpointIds = discoverer.getDevices();
//
//        // hardcoded device ids
//        endpointIds = Arrays.asList("a", "b", "c");
//        System.out.println("end points found: " + endpointIds);
//        // hardcoded list of restaurants
//        List<Restaurant> restaurantsFound = getRestaurantData();
//
//        for (String endpointId : endpointIds) {
//            Messenger messenger = new Messenger(context, discoverer.DEVICE_NAME);
//            // TODO: uncomment this when ready
////            messenger.get(endpointId, ResourceURIs.MENU, (String response) -> {
////                Gson gson = new Gson();
////                Restaurant restaurant = gson.fromJson(response, Restaurant.class);
////                notifyListenerToAddRestaurant(restaurant);
////            });
//
//            // hardcoded response
//            notifyListenerToAddRestaurant(restaurantsFound.get(endpointIds.indexOf(endpointId)));
//        }
//    }


//    // TODO: replace hard-coded order info
//    public static Restaurant makeSteakHouse() {
//        List<Dish> westernDishes = new ArrayList<>();
//        westernDishes.add(new Dish("Sirloin", 12.50));
//        westernDishes.add(new Dish("Rib eye", 13.50));
//        westernDishes.add(new Dish("Angus Beef", 14.50));
//
//        return new Restaurant("Steak House", "Western", westernDishes);
//    }


    public static List<Order> getConfirmedOrder () {

        Restaurant steakHouse =  new Restaurant("Steak House", "Western");
        List<Dish> western_one = new ArrayList<>();
        western_one.add(new Dish("Sirloin", 12.50));
        Order order_one = Order.confirmOrder(new UserProfile("Alice"), steakHouse, western_one);

        List<Dish> western_two = new ArrayList<>();
        western_two.add(new Dish("Rib eye", 13.50));
        western_two.add(new Dish("Angus Beef", 14.50));
        Order order_two = Order.confirmOrder(new UserProfile("Bob"), steakHouse, western_two);


        List<Order> orders = new ArrayList<>();
        orders.add(order_one);
        orders.add(order_two);

        return orders;
    }

    // Classes that want to observe changes in the list of restaurants discovered
    // should implement this interface to get notified.
    public interface OrderListener {
        void onNewOrder(Order order);
    }
}

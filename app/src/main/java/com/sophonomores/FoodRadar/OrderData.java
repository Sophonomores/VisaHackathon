package com.sophonomores.FoodRadar;

import android.content.Context;

import com.sophonomores.FoodRadar.entities.Order;

public class OrderData {

    private Context context;
    private static OrderListener listener;

    public OrderData(Context context) {
        this.context = context;
    }

    public void setOrderListener(OrderListener listener) {
        this.listener = listener;
    }

    public static int notifyListenerToAddOrder(Order order) {
        return listener.onNewOrder(order);
//        System.out.println("New order coming from: " +  order.getCustomerName());
    }

//    public static List<Order> getConfirmedOrder () {
//
//        Restaurant steakHouse =  RestaurantData.makeSteakHouse();
//        List<Dish> western_one = new ArrayList<>();
//        western_one.add(new Dish("Sirloin", 12.50));
//        Order order_one = Order.confirmOrder(new UserProfile("Alice"), steakHouse, western_one);
//
//        List<Dish> western_two = new ArrayList<>();
//        western_two.add(new Dish("Rib eye", 13.50));
//        western_two.add(new Dish("Angus Beef", 14.50));
//        Order order_two = Order.confirmOrder(new UserProfile("Bob"), steakHouse, western_two);
//
//
//        List<Order> orders = new ArrayList<>();
//        orders.add(order_one);
//        orders.add(order_two);
//
//        return orders;
//    }

    // Classes that want to observe changes in the list of orders received
    // should implement this interface to get notified.
    public interface OrderListener {
        int onNewOrder(Order order);
    }
}

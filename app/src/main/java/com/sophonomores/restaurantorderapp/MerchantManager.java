package com.sophonomores.restaurantorderapp;

import android.content.Context;

import com.sophonomores.restaurantorderapp.entities.Restaurant;
import com.sophonomores.restaurantorderapp.entities.Order;

import java.util.ArrayList;
import java.util.List;

public class MerchantManager implements OrderData.OrderListener {

    // enforce Singleton pattern
    private static MerchantManager instance;

    private List<Order> orderList;
    private Restaurant restaurant;
    private OrderData orderData;
    private int orderId = 1;

    // register observer
    private OrderListener listener;

    public MerchantManager(Context context) {
        this.orderData = new OrderData(context);
        this.orderList = new ArrayList<>();
        this.restaurant = null;
    }

    public static MerchantManager init(Restaurant restaurant, Context context) {
        instance = new MerchantManager(context);
        instance.restaurant = restaurant;
        return instance;
    }

    public static MerchantManager getInstance() {
        if (instance == null) {
            instance = new MerchantManager(null); // null for now
        }

        return instance;
    }

    public static boolean isInitialised() {
        return instance != null;
    }

    public void setOrderListener(OrderListener listener) {
        this.listener = listener;
    }

    public void startReceivingOrders() {
        orderData.setOrderListener(this);
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void markAsReady(Order order) {
        order.setReadyToServe();
    }

    @Override
    public int onNewOrder(Order order) {
        order.setId(orderId);
        orderList.add(order);
        listener.onOrderDataChange();
        orderId++;
        return order.getId();
    }

    // This interface is to register UI to observer changes in the list of orders in
    // MerchantManager class.
    public interface OrderListener {
        void onOrderDataChange();

        // TODO: next sprint - able to update the status of the order
        // void onOrderStatusChange();
    }

    public int getOrderStatus(int id) {
        return orderList.stream().filter((Order o) -> {
            return o.getId() == id;
        }).findFirst().get().getStatus();
    }
}

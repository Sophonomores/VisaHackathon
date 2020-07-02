package com.sophonomores.FoodRadar;

import android.content.Context;

import com.sophonomores.FoodRadar.entities.Dish;
import com.sophonomores.FoodRadar.entities.Restaurant;
import com.sophonomores.FoodRadar.entities.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public void markAsCollected(Order order) {
        order.setCollected();
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

    public void pauseDish(String dishName) {
        restaurant.pauseDish(dishName);
    }

    public void continueDish(String dishName) {
        restaurant.continueDish(dishName);
    }

    public List<Dish> checkOrderAvailability(Order order) {
        return order.getDishes().stream().filter((Dish d) -> {
            return !getRestaurant().getDish(d.getName()).getAvailability();
        }).collect(Collectors.toList());
    }
}

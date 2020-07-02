package com.sophonomores.FoodRadar;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sophonomores.FoodRadar.entities.Dish;
import com.sophonomores.FoodRadar.entities.Order;
import com.sophonomores.FoodRadar.entities.Restaurant;
import com.sophonomores.FoodRadar.entities.ShoppingCart;
import com.sophonomores.FoodRadar.entities.UserProfile;
import com.sophonomores.FoodRadar.services.Discoverer;
import com.sophonomores.FoodRadar.services.Messenger;
import com.sophonomores.FoodRadar.services.api.ResourceURIs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class handles the main business logic of the customer app.
 * View (various Activity classes) should access the app through this class.
 */
public class OrderManager implements RestaurantData.RestaurantsChangeListener {

    public static int USE_OFFLINE_PAYMENT = 0;
    public static int USE_VISA_CHECKOUT = 1;

    // enforce Singleton pattern
    private static OrderManager instance;

    private List<Restaurant> restaurantList;
    private Restaurant currentRestaurant;
    private UserProfile user;
    private ShoppingCart cart;
    private RestaurantData restaurantData;
    private List<Order> pastOrders;
    private int paymentMode;

    // register observer
    private RestaurantsChangeListener listener;

    private OrderManager(Context context) {
        this.restaurantData = new RestaurantData(context);
        this.restaurantList = new ArrayList<>();
        this.user = null;
        this.cart = new ShoppingCart();
        this.pastOrders = new ArrayList<>();
        this.paymentMode = 0;
    }

    public void startSearchingForRestaurants(Runnable r) {
        restaurantData.setRestaurantsChangeListener(this);
        restaurantData.getListOfNearbyRestaurants(r);
    }

    public static OrderManager init(UserProfile user, Context context) {
        instance = new OrderManager(context);
        instance.user = user;
        return instance;
    }

    public static OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager(null); // null for now
        }

        return instance;
    }

    public static boolean isInitialised() {
        return instance != null;
    }

    public List<Restaurant> getRestaurantList() {
        return restaurantList;
    }

    public void setRestaurantList(List<Restaurant> restaurantList) {
        if (this.restaurantList == null) {
            this.restaurantList = new ArrayList<>();
        }
        // to keep any existing references to this list referring to the correct list
        this.restaurantList.clear();
        this.restaurantList.addAll(restaurantList);
    }

    public UserProfile getUser() {
        return user;
    }

    public ShoppingCart getCart() {
        return cart;
    }

    public void addDishToCart(Dish dish) {
        cart.addDish(dish);
    }

    public void removeDishFromCart(Dish dish) {
        cart.removeDish(dish);
    }

    public void clearShoppingCart() {
        cart.clear();
    }

    public double getCartTotalPrice() {
        return cart.getTotalPrice();
    }

    public Restaurant getCurrentRestaurant() {
        return currentRestaurant;
    }

    public void setCurrentRestaurant(Restaurant r) {
        currentRestaurant = r;
    }

    public List<Order> getPastOrders() {
        return this.pastOrders;
    }

    public Optional<Order> getOrder(int id) {
        return this.pastOrders.stream().filter((Order o) -> {return o.getId() == id;}).findFirst();
    }

    public void addPastOrder(Order o) {
        this.pastOrders.add(o);
    }

    public void setRestaurantsChangeListener(RestaurantsChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onRestaurantsChange(List<Restaurant> restaurants) {
        System.out.println("setting restaurant list...");
        setRestaurantList(restaurants);
        // update UI that observes this OrderManager class
        listener.onRestaurantsChange();
    }

    @Override
    public void onRestaurantAdded(Restaurant restaurant) {
        if (!restaurantList.contains(restaurant)) {
            restaurantList.add(restaurant);
        }
        // update UI that observes this OrderManager class
        listener.onRestaurantsChange();
    }

    // This interface is to register UI to observer changes in the list of restaurants in
    // OrderManager class.
    public interface RestaurantsChangeListener {
        void onRestaurantsChange();
    }

    // Assume all orders come from one restaurant for demo.
    public void refreshOrderStatus(Context c, Runnable r) {
        String restaurantId = "";
        List<Integer> orderIds = new ArrayList<>();
        boolean sendMsg = false;
        for (Order o : pastOrders) {
            if (o.getStatus() == Order.CONFIRMED || o.getStatus() == Order.READY_TO_SERVE) {
                restaurantId = o.getRestaurantId();
                orderIds.add(o.getId());
                sendMsg = true;
            }
        }
        if (sendMsg) {
            new Messenger(c, Discoverer.DEVICE_NAME)
                    .post(restaurantId,
                            ResourceURIs.STATUS,
                            new Gson().toJson(orderIds),
                            (String response) -> {
                                System.out.println("Response: " + response);
                                List<Integer> status = new Gson().fromJson(response, new TypeToken<ArrayList<Integer>>(){}.getType());
                                for(int i = 0; i < orderIds.size(); i++) {
                                    getOrder(orderIds.get(i)).get().setStatus(status.get(i));
                                }
                                r.run();
                            });
        }
    }

    public int getPaymentMode() {
        return this.paymentMode;
    }

    public void setPaymentMode(int mode) {
        this.paymentMode = mode;
    }
}

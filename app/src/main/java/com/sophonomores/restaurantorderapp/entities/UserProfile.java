package com.sophonomores.restaurantorderapp.entities;

public class UserProfile {

    private String username;
    // TODO: implement more fields such as userid, PAN, etc. as needed.

    public UserProfile(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}

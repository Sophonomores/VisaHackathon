package com.sophonomores.restaurantorderapp.services.api;

public class Parser {
    public static Request parseRequest(String input) {
        String method = input.substring(0, input.indexOf(':'));
        String uri = input.substring(input.indexOf(':') + 1);

        return new Request(uri, method);
    }
}

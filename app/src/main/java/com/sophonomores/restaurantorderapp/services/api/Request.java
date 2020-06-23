package com.sophonomores.restaurantorderapp.services.api;

public class Request {
    private final String uri;
    private final String method;

    public Request(String uri, String method) {
        this.uri = uri;
        this.method = method;
    }

    public String getUri() {
        return this.uri;
    }

    public String getMethod() {
        return this.method;
    }
}

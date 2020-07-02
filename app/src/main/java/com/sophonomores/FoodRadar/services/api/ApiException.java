package com.sophonomores.FoodRadar.services.api;

public class ApiException extends Exception {
    private String message;
    private String statusCode;

    public ApiException(String message, String statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return this.message;
    }

    public String getStatusCode() {
        return this.statusCode;
    }
}

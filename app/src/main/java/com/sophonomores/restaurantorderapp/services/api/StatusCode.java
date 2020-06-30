package com.sophonomores.restaurantorderapp.services.api;

public class StatusCode {
    public static final String OK = "200 OK";
    public static final String PAYMENT_DECLINED = "402 Payment Required";
    public static final String NOT_FOUND = "404 Not Found";
    public static final String METHOD_NOT_ALLOWED = "405 Method Not Allowed";
    public static final String REQUEST_TIMEOUT = "408 Request Timeout";
    public static final String INTERNAL_SERVER_ERROR = "500 Internal Server Error";
    private static final String UNKNOWN_ERROR = "Unknown Error";

    /**
     * Converts a status code into its string representation.
     */
    public static String convert(int statusCode) {
        switch (statusCode){
            case 200:
                return OK;
            case 402:
                return PAYMENT_DECLINED;
            case 404:
                return NOT_FOUND;
            case 405:
                return METHOD_NOT_ALLOWED;
            case 408:
                return REQUEST_TIMEOUT;
            case 500:
                return INTERNAL_SERVER_ERROR;
            default:
                return UNKNOWN_ERROR;
        }
    }
}

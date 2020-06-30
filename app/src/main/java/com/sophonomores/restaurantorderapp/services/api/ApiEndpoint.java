package com.sophonomores.restaurantorderapp.services.api;

import java.util.HashMap;
import java.util.Map;

public class ApiEndpoint {
    private static final Map<String, Map<String, Class<? extends Action>>> ACTION_MAPPINGS = new HashMap<>();

    static {
        map(ResourceURIs.INFO, ApiMethod.GET, GetInfoAction.class);
        map(ResourceURIs.CHECKOUT, ApiMethod.POST, PostCheckoutAction.class);
        map(ResourceURIs.STATUS, ApiMethod.POST, PostOrderStatusAction.class);
    }

    private static void map(String uri, String method, Class<? extends Action> actionClass) {
        ACTION_MAPPINGS.computeIfAbsent(uri, k -> new HashMap<>()).put(method, actionClass);
    }

    public static Action getAction(String uri, String method) throws ApiException {
        if (!ACTION_MAPPINGS.containsKey(uri)) {
            throw new ApiException("Resource with URI " + uri + " is not found.", StatusCode.NOT_FOUND);
        }
        Class<? extends Action> controllerClass =
                ACTION_MAPPINGS.getOrDefault(uri, new HashMap<>()).get(method);
        if (controllerClass == null) {
            throw new ApiException("Method [" + method + "] is not allowed for URI " + uri + ".",
                    StatusCode.METHOD_NOT_ALLOWED);
        }

        try {
            return controllerClass.newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}

package com.sophonomores.restaurantorderapp.services.api;

import androidx.annotation.Nullable;

public class PostCheckoutAction extends Action {
    @Override
    public String execute(@Nullable String input) {
        System.out.println(input);
        return StatusCode.OK;
    }
}

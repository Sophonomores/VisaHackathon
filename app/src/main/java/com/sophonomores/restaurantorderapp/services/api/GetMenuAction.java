package com.sophonomores.restaurantorderapp.services.api;

import androidx.annotation.Nullable;

public class GetMenuAction extends Action {
    @Override
    public String execute(@Nullable String input) {
        return StatusCode.OK;
    }
}
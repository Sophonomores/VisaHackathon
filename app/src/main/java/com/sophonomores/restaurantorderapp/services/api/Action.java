package com.sophonomores.restaurantorderapp.services.api;

import androidx.annotation.Nullable;

public abstract class Action {
    public abstract String execute(@Nullable String input);
}

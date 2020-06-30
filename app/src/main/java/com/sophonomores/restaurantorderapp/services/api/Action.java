package com.sophonomores.restaurantorderapp.services.api;

import android.content.Context;

import java.util.function.Consumer;

import androidx.annotation.Nullable;

public abstract class Action {
    public abstract void execute(@Nullable String input, Context context, Consumer<String> consumer);
}

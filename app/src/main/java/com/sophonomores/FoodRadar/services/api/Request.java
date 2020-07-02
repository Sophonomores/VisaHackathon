package com.sophonomores.FoodRadar.services.api;

import androidx.annotation.Nullable;

public class Request {
    private final String uri;
    private final String method;
    private final String content;

    public Request(String uri, String method, @Nullable String content) {
        this.uri = uri;
        this.method = method;
        this.content = content;
    }

    public String getUri() {
        return this.uri;
    }

    public String getMethod() {
        return this.method;
    }

    public String getContent() {
        return this.content;
    }
}

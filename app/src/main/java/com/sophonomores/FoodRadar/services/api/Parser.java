package com.sophonomores.FoodRadar.services.api;

public class Parser {
    public static Request parseRequest(String input) {
        String method = input.substring(0, input.indexOf(':'));
        String temp = input.substring(input.indexOf(':') + 1);
        String uri;
        String content = null;
        if (temp.indexOf(':') != -1) {
            uri = temp.substring(0, temp.indexOf(':'));
            content = temp.substring(temp.indexOf(':') + 1);
        } else {
            uri = temp;
        }

        return new Request(uri, method, content);
    }
}

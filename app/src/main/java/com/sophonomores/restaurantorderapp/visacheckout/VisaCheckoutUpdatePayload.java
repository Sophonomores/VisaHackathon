package com.sophonomores.restaurantorderapp.visacheckout;

/**
 * VisaCheckoutUpdatePayload build the payload for update API.
 */
public class VisaCheckoutUpdatePayload {
    public enum EventType {
        confirm("Confirm"), cancel("Cancel");

        // Value is the acceptable value for the json payload.
        public final String value;
        EventType(String value) {
            this.value = value;
        }
    }

    public VisaCheckoutUpdatePayload.EventType eventType = VisaCheckoutUpdatePayload.EventType.confirm;
    public Double total = 25.61;
    public String currencyCode = "USD";

    @Override
    public String toString() {
        // Note that, there should be no whitespace in payload.
        // Otherwise, generating x-pay-token will face some issues.
        return "{\"orderInfo\":" +
                "{\"currencyCode\":\"" + currencyCode + "\"," +
                "\"eventType\":\"" + eventType.value + "\"," +
                "\"total\":\"" + total + "\"}}";
    }
}

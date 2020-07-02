package com.sophonomores.FoodRadar.vpp;

/**
 * VppAuthorizationPayload constructs the payload for VPP Authorization API conveniently.
 */
public class VppAuthorizationPayload {
    // These fields are initialized with hard-coded values.
    public String pan = "4111111111111111";
    public String panExpiryDate = "2023-12";
    public Double transactionAmount = 51.29;

    @Override
    public String toString() {
        return  " {" +
                "  \"acctInfo\": {" +
                "    \"primryAcctNum\": {" +
                "      \"pan\": \"" + pan + "\"," +
                "      \"panExpDt\": \"" + panExpiryDate + "\"" +
                "    }" +
                "  }," +
                "  \"cardAcceptr\": {" +
                "    \"clientId\": \"0123456789012345678901234567893\"" +
                "  }," +
                "  \"msgIdentfctn\": {" +
                "    \"correlatnId\": \"14bc567d90f23e56a8f045\"" +
                "  }," +
                "  \"transctn\": {" +
                "    \"tranAmt\": {" +
                "      \"amt\": \"" + transactionAmount + "\"" +
                "    }" +
                "  }" +
                "}";
    }
}

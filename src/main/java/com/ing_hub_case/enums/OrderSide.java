package com.ing_hub_case.enums;

public enum OrderSide {
    BUY("BUY"),
    SELL("SELL"),
    MATCH("MATCH"),
    CANCEL("CANCEL");


    final String value;
    OrderSide(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return value;
    }
}

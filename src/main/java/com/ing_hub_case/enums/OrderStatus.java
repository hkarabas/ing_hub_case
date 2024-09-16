package com.ing_hub_case.enums;

public enum OrderStatus {
    PENDING("PENDING"),
    MATCHED("MATCHED"),
    CANCELED("CANCELED");

    final String value;
    OrderStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

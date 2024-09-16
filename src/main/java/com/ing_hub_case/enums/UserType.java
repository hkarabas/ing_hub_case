package com.ing_hub_case.enums;

public  enum UserType {
    ADMIN("ADMIN"),
    BROKER("BROKER"),
    CUSTOMER("CUSTOMER");

    final String value;
    UserType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}

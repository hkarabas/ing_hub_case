package com.ing_hub_case.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CustomerAmountDto {

    private Integer customerId;

    @NotNull(message = "Customer Cannot be null")
    private UserDto customer;

    @NotNull(message = "Iban Cannot be null" )
    @Max(32)
    @Min(16)
    private String iban;

    @NotNull(message ="amount")
    @Min(1)
    private Double amount;

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public UserDto getCustomer() {
        return customer;
    }

    public void setCustomer(UserDto customer) {
        this.customer = customer;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}

package com.ing_hub_case.models;

import com.ing_hub_case.enums.Currency;
import com.ing_hub_case.enums.UserType;
import com.ing_hub_case.utils.EnumNamePattern;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class UserDto {

    private Integer id;

    @NotNull(message = "Name is Can not be null")
    private String name;

    @NotNull(message = "Last Name is Can not be null")
    private String lastName;

    @NotNull(message = "Password is Can not be null")
    private String password;

    @NotNull(message = "Email is Can not be null")
    private String email;

    @NotNull(message = "Iban is Can not be null\"")
    private String iban;


    @EnumNamePattern(regexp = "ADMIN|BROKER|CUSTOMER")
    private UserType userType;

    @NotNull(message = "Currency is Can not be null")
    @EnumNamePattern(regexp = "TRY|USD|EU")
    private Currency defaultCurrency;

    private List<OrderDto> orderDtoList;

    private List<CustomerAmountDto> customerAmountDtos;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public @EnumNamePattern(regexp = "BROKER|CUSTOMER") UserType getUserType() {
        return userType;
    }

    public void setUserType(@EnumNamePattern(regexp = "BROKER|CUSTOMER") UserType userType) {
        this.userType = userType;
    }

    public @EnumNamePattern(regexp = "TRY|USD|EU") Currency getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(@EnumNamePattern(regexp = "TRY|USD|EU") Currency defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public List<OrderDto> getOrderDtoList() {
        return orderDtoList;
    }

    public void setOrderDtoList(List<OrderDto> orderDtoList) {
        this.orderDtoList = orderDtoList;
    }

    public List<CustomerAmountDto> getCustomerAmountDtos() {
        return customerAmountDtos;
    }

    public void setCustomerAmountDtos(List<CustomerAmountDto> customerAmountDtos) {
        this.customerAmountDtos = customerAmountDtos;
    }
    public @NotNull(message = "Iban is Can not be null\"") String getIban() {
        return iban;
    }

    public void setIban(@NotNull(message = "Iban is Can not be null\"") String iban) {
        this.iban = iban;
    }



}

package com.ing_hub_case.models;

import com.ing_hub_case.entities.User;
import com.ing_hub_case.enums.OrderSide;
import com.ing_hub_case.enums.OrderStatus;
import com.ing_hub_case.utils.EnumNamePattern;
import jakarta.validation.constraints.NotNull;

import java.util.Date;


public class OrderDto {

    private Integer id;

    @NotNull(message = "Customer Cannot be null")
    private Integer customerId;

    private User customer;

    @NotNull(message = "Asset Cannot be null")
    private Integer assetId;

    private AssetDto asset;

    @NotNull(message = "Order Side Cannot be null")
    @EnumNamePattern(regexp = "BUY|SELL|CANCEL|MATCH")
    private OrderSide orderSide;

    private Integer size;

    private Double price;

    private String currency;

    @EnumNamePattern(regexp = "PENDING|MATCHED|CANCELED")
    private OrderStatus status;

    private Date createDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public AssetDto getAsset() {
        return asset;
    }

    public void setAsset(AssetDto asset) {
        this.asset = asset;
    }

    public @EnumNamePattern(regexp = "BUY|SELL|CANCEL|MATCH") OrderSide getOrderSide() {
        return orderSide;
    }

    public void setOrderSide(@EnumNamePattern(regexp = "BUY|SELL|CANCEL|MATCH") OrderSide orderSide) {
        this.orderSide = orderSide;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public  OrderStatus getStatus() {
        return status;
    }

    public void setStatus(@EnumNamePattern(regexp = "PENDING|MATCHED|CANCELED") OrderStatus status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}

package com.ing_hub_case.models;

import com.ing_hub_case.entities.Asset;
import com.ing_hub_case.entities.Order;
import com.ing_hub_case.entities.User;

import java.sql.Date;

public class OrderTradeDto {

    private Integer quantity;
    private Double price;
    private Date tradeTime;
    private User customer;
    private Asset asset;
    private User  matchedAdmin;
    private Order order;

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Date tradeTime) {
        this.tradeTime = tradeTime;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public User getMatchedAdmin() {
        return matchedAdmin;
    }

    public void setMatchedAdmin(User matchedAdmin) {
        this.matchedAdmin = matchedAdmin;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}

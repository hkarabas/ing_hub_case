package com.ing_hub_case.entities;

import jakarta.persistence.*;

import java.sql.Date;
import java.sql.Timestamp;

@Table(name="order_trade")
@Entity
public class OrderTrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name="asset_id")
    private Integer assetId;

    @Column(name="order_id")
    private Integer orderId;

    private Integer quantity;

    @Column(name = "matched_admin_id")
    private Integer matchedAdminId;

    private Double price;

    private Timestamp tradeTime;

    @OneToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id",insertable = false,updatable = false)
    private User customer;

    @OneToOne
    @JoinColumn(name = "asset_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Asset asset;

    @OneToOne
    @JoinColumn(name = "admin_id", referencedColumnName = "id",insertable = false,updatable = false)
    private User matchedAdmin;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Order order;

    public Order getOrder() {
        return order;
    }

    public User getCustomer() {
        return customer;
    }

    public Asset getAsset() {
        return asset;
    }


    public Integer getId() {
        return id;
    }

    public Integer getMatchedAdminId() {
        return matchedAdminId;
    }

    public void setMatchedAdminId(Integer matchedAdminId) {
        this.matchedAdminId = matchedAdminId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public User getMatchedAdmin() {
        return matchedAdmin;
    }

    public void setMatchedAdmin(User matchedAdmin) {
        this.matchedAdmin = matchedAdmin;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Timestamp getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Timestamp tradeTime) {
        this.tradeTime = tradeTime;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

}
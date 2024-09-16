package com.ing_hub_case.entities;


import com.ing_hub_case.enums.OrderSide;
import com.ing_hub_case.enums.OrderStatus;
import com.ing_hub_case.models.OrderDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;

@Table(name = "order_table")
@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;


    @Column(name = "customer_id")
    private Integer customerId;


    @Column(name = "asset_id")
    private Integer  assetId;


    @Column(name = "order_side")
    private String orderSide;

    @ManyToOne
    @JoinColumn(name = "customer_id",insertable = false,updatable = false)
    private User customer;

    @OneToOne
    @JoinColumn(name = "asset_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Asset asset;

    @OneToOne(mappedBy = "order")
    private OrderTrade orderTrade;



    @Min(1)
    private Integer size;

    @Min(1)
    private Double price;

    private String currency;

    private String status;

    @Column(name = "create_date")
    private Timestamp createDate;


    public OrderDto convertDto() {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(this.id);
        orderDto.setAssetId(this.assetId);
        orderDto.setCustomerId(this.customerId);
        orderDto.setCreateDate(this.createDate);
        orderDto.setCurrency(this.currency);
        orderDto.setPrice(this.price);
        orderDto.setSize(this.size);
        orderDto.setStatus(OrderStatus.valueOf(this.status));
        orderDto.setOrderSide(OrderSide.valueOf(this.orderSide));
        return orderDto;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public @NotNull(message = "Customer Cannot be null") Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(@NotNull(message = "Customer Cannot be null") Integer customerId) {
        this.customerId = customerId;
    }

    public @NotNull(message = "Asset Cannot be null") Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(@NotNull(message = "Asset Cannot be null") Integer assetId) {
        this.assetId = assetId;
    }

    public @NotNull(message = "Asset Cannot be null") String getOrderSide() {
        return orderSide;
    }

    public void setOrderSide(@NotNull(message = "Asset Cannot be null") String orderSide) {
        this.orderSide = orderSide;
    }

    public @Min(1) Integer getSize() {
        return size;
    }

    public void setSize(@Min(1) Integer size) {
        this.size = size;
    }

    public @Min(1) Double getPrice() {
        return price;
    }

    public void setPrice(@Min(1) Double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public User getCustomer() {
        return customer;
    }

    public Asset getAsset() {
        return asset;
    }
    public OrderTrade getOrderTrade() {
        return orderTrade;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }


}

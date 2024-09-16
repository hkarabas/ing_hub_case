package com.ing_hub_case.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


@Table(name = "customer_portfolio")
@Entity
public class CustomerPortfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer id;

    @NotNull(message = "Customer Cannot be null")
    @Column(name = "customer_id")
    private Integer customerId;

    @NotNull(message = "Asset Cannot be null")
    @Column(name="asset_id")
    private Integer assetId;

    @OneToOne
    @JoinColumn(name = "customer_id",insertable = false,updatable = false)
    private User customer;

    @OneToOne
    @JoinColumn(name = "asset_id", referencedColumnName = "id",insertable = false,updatable = false)
    private Asset asset;

    @NotNull(message = "Size Cannot be null")
    @Min(1)
    private Integer size;

    @Min(1)
    @Column(name = "total_price")
    private Double totalPrice;


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

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}

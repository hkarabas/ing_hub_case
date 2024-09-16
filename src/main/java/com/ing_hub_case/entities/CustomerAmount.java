package com.ing_hub_case.entities;

import jakarta.persistence.*;


@Table(name = "customer_amount")
@Entity
public class CustomerAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(name = "customer_id")
    private Integer customerId;

    private String iban;

    private Double amount;


    @OneToOne
    @JoinColumn(name = "customer_id",insertable = false,updatable = false)
    private User customer;


    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public User getCustomer() {
        return customer;
    }

}

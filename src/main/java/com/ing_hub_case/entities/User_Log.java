package com.ing_hub_case.entities;


import jakarta.persistence.*;
import java.sql.Timestamp;

@Table(name = "user_log")
@Entity
public class User_Log {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer id;

    @Column(name = "user_id",nullable = false)
    private Integer user_id;

    @Column(name = "created_date")
    private Timestamp created_date;

    @Column(name = "updated_date")
    private Timestamp updated_date;

    @Column(name="json")
    private String json;

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Timestamp getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Timestamp created_date) {
        this.created_date = created_date;
    }

    public Timestamp getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(Timestamp updated_date) {
        this.updated_date = updated_date;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}

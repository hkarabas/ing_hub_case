package com.ing_hub_case.repositories;


import com.ing_hub_case.entities.OrderTrade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderTradeRepository extends JpaRepository<OrderTrade,Integer> {
}

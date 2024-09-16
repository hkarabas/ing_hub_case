package com.ing_hub_case.repositories;

import com.ing_hub_case.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {
   List<Order> findAllByCustomerIdAndCreatedDateBetween(Integer customerId, Date beginDate,Date endDate);
}

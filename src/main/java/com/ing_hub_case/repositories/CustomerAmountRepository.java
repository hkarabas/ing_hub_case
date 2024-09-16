package com.ing_hub_case.repositories;

import com.ing_hub_case.entities.CustomerAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerAmountRepository extends JpaRepository<CustomerAmount,Integer> {

    Optional<CustomerAmount>  findByIban(String iban);

}

package com.ing_hub_case.repositories;


import com.ing_hub_case.entities.User_Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLogRepository extends JpaRepository<User_Log,Integer> {

}

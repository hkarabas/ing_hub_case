package com.ing_hub_case.feignclients;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name ="customerbalanceclient",url ="http://localhost:8085/customeramount" )
public interface CustomerBalanceClient {

    @PostMapping("/withdraw")
    Boolean postWithDraw(@RequestParam String iban, @RequestParam Double price, @RequestHeader("Authorization") String token);

    @PostMapping("/deposit")
    Boolean postDeposit(@RequestParam String iban, @RequestParam Double price, @RequestHeader("Authorization") String token);

}

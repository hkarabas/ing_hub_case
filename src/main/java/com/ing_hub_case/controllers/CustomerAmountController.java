package com.ing_hub_case.controllers;


import com.ing_hub_case.services.CustomerAmountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/customeramount")
@RestController
public class CustomerAmountController {

    public CustomerAmountController(CustomerAmountService customerAmountService) {
        this.customerAmountService = customerAmountService;
    }

    private final CustomerAmountService customerAmountService;

    @PostMapping("/withdraw")
    public ResponseEntity<Boolean> withDraw(@RequestParam String iban,@RequestParam Double price) {
         return new ResponseEntity<Boolean>(customerAmountService.withDrawMoney(iban,price), HttpStatus.ACCEPTED);
    }
    @PostMapping("/deposit")
    public ResponseEntity<Boolean> depositDraw(@RequestParam String iban,@RequestParam Double price) {
        return new ResponseEntity<Boolean>(customerAmountService.depositMoney(iban,price), HttpStatus.ACCEPTED);
    }

}

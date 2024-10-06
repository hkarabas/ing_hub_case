package com.ing_hub_case.controllers;


import com.ing_hub_case.factorty.OrderFactory;
import com.ing_hub_case.models.OrderDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RequestMapping("/order")
@RestController
public class OrderController {

    private final OrderFactory orderFactory;

    public OrderController(OrderFactory orderFactory) {
        this.orderFactory = orderFactory;
    }


    @PostMapping("/doAction")
    public ResponseEntity<OrderDto> doAction(@RequestBody OrderDto orderDto) {
         return orderFactory.getOrderService(orderDto.getOrderSide().toString()).doAction(orderDto);

    }


}

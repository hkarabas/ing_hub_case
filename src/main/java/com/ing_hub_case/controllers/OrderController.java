package com.ing_hub_case.controllers;


import com.ing_hub_case.models.AssetDto;
import com.ing_hub_case.models.OrderDto;
import com.ing_hub_case.services.OrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RequestMapping("/order")
@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto) {
        return  new ResponseEntity<OrderDto>(orderService.createOrder(orderDto), HttpStatus.CREATED);
    }

    @PostMapping("/matched")
    public ResponseEntity<OrderDto> matchedOrder(@RequestParam Integer orderID) {
        return new ResponseEntity<OrderDto>(orderService.setMatched(orderID),HttpStatus.ACCEPTED);
    }

    @PostMapping("/canceled")
    public ResponseEntity<OrderDto> canceledOrder(@RequestParam Integer orderID) {
        return new ResponseEntity<OrderDto>(orderService.setCanceled(orderID),HttpStatus.ACCEPTED);
    }

    @GetMapping("/orderList")
    public ResponseEntity<List<OrderDto>> getOrderList(@RequestParam Integer customerId,
                                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")  Date  beginDate,
                                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")  Date  endDate) {
        return ResponseEntity.ok(orderService.getOrderListByCustomerAndDateRange(customerId, beginDate,endDate));
    }

}

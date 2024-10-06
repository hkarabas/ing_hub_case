package com.ing_hub_case.factorty;

import com.ing_hub_case.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderFactory {

    @Autowired
    private BuyOrderService buyOrderService;
    @Autowired
    private  SellOrderService sellOrderService;
    @Autowired
    private MatchedOrderService matchedOrderService;
    @Autowired
    private  OrderCancelService orderCancelService;


    public IOrder getOrderService(String status) {
       switch (status) {
           case "BUY":
               return  buyOrderService;
           case "SELL":
               return sellOrderService;
           case "CANCEL":
               return orderCancelService;
           case "MATCH":
               return  matchedOrderService;
       }
       return null;
   }

}

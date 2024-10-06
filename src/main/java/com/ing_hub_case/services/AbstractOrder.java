package com.ing_hub_case.services;

import com.ing_hub_case.entities.Asset;
import com.ing_hub_case.entities.Order;
import com.ing_hub_case.entities.User;
import com.ing_hub_case.feignclients.CustomerBalanceClient;
import com.ing_hub_case.models.OrderDto;
import com.ing_hub_case.repositories.AssetRepository;
import com.ing_hub_case.repositories.OrderRepository;
import com.ing_hub_case.repositories.OrderTradeRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractOrder {

    protected final OrderRepository orderRepository;
    protected final AssetRepository assetRepository;
    protected final CustomerBalanceClient customerBalanceClient;
    protected final  JwtService jwtService;
    protected final OrderTradeRepository orderTradeRepository;


    protected AbstractOrder(OrderRepository orderRepository, AssetRepository assetRepository, CustomerBalanceClient customerBalanceClient, JwtService jwtService, OrderTradeRepository orderTradeRepository) {
        this.orderRepository = orderRepository;
        this.assetRepository = assetRepository;
        this.customerBalanceClient = customerBalanceClient;
        this.jwtService = jwtService;
        this.orderTradeRepository = orderTradeRepository;
    }

    protected User getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User)authentication.getPrincipal();
    }
    protected boolean withDrawMoney(String iban,Double price) {
        String token  = "Bearer "+jwtService.generateToken(getUser());
        return customerBalanceClient.postWithDraw(iban,price,token);
    }
    protected Optional<Asset> assetControl(Integer assetId) {
        return assetRepository.findById(assetId);
    }

    public List<OrderDto> getOrderListByCustomerAndDateRange(Integer customerId, java.util.Date  beginDate, java.util.Date endDate) {
        Date beginDate_L = new Date(beginDate.getTime());
        Date endDate_L = new Date(endDate.getTime());
        return orderRepository.findAllByCustomerIdAndCreatedDateBetween(customerId,beginDate_L,endDate_L).stream().map(Order::convertDto).collect(Collectors.toList());
    }

    protected boolean depositMoney(String iban,Double price) {
        String token  = "Bearer "+jwtService.generateToken(getUser());
        return customerBalanceClient.postDeposit(iban,price,token);
    }


}

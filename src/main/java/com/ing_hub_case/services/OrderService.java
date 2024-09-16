package com.ing_hub_case.services;


import com.ing_hub_case.entities.Asset;
import com.ing_hub_case.entities.Order;
import com.ing_hub_case.entities.OrderTrade;
import com.ing_hub_case.entities.User;
import com.ing_hub_case.enums.OrderSide;
import com.ing_hub_case.enums.OrderStatus;
import com.ing_hub_case.enums.UserType;
import com.ing_hub_case.exception.AssetUsableSizeException;
import com.ing_hub_case.exception.CustomerBalanceException;
import com.ing_hub_case.exception.NoSuchOrderExistsException;
import com.ing_hub_case.exception.UserUnAuthorizedException;
import com.ing_hub_case.feignclients.CustomerBalanceClient;
import com.ing_hub_case.models.OrderDto;
import com.ing_hub_case.repositories.AssetRepository;
import com.ing_hub_case.repositories.OrderRepository;
import com.ing_hub_case.repositories.OrderTradeRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;
    private final OrderTradeRepository orderTradeRepository;
    private final CustomerBalanceClient customerBalanceClient;
    private final  JwtService jwtService;

    public OrderService(OrderRepository orderRepository, AssetRepository assetRepository, OrderTradeRepository orderTradeRepository, CustomerBalanceClient customerBalanceClient, JwtService jwtService) {
        this.orderRepository = orderRepository;
        this.assetRepository = assetRepository;
        this.orderTradeRepository = orderTradeRepository;
        this.customerBalanceClient = customerBalanceClient;
        this.jwtService = jwtService;
    }
    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {

        if (orderDto.getOrderSide( )== OrderSide.BUY) {
            return buyAsset(orderDto);
        }
         return sellAsset(orderDto);

    }
    private User  getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User)authentication.getPrincipal();
    }

    private  OrderDto buyAsset(OrderDto orderDto) {
        if (!Objects.isNull(orderDto.getId())) {
            throw new IllegalArgumentException("Order is should be new a request!! ");
        }
        if (!getUser().getUserType().equals(UserType.CUSTOMER.toString())) {
            throw new UserUnAuthorizedException(String.format("Buy for (CUSTOMER) UnAuthorized user type {0}",getUser().getUserType()));
        }
        if (!getUser().getDefaultCurrency().equals(orderDto.getCurrency())) {
            throw  new IllegalArgumentException("Order Currency can not match Customer Default Currency");
        }
        if (!withDrawMoney(getUser().getIban(), orderDto.getPrice())) {
            throw  new CustomerBalanceException("Customer Amount Balance Enough");
        }
        Asset asset = assetControl(orderDto.getAssetId()).orElseThrow(()->new IllegalArgumentException("Asset there is not found"));
        if ((asset.getUsableSize()-orderDto.getSize())<0) {
            throw new AssetUsableSizeException(String.format("Asset usable size not enough usable size {0}",asset.getUsableSize()));
        }

        Order order = new Order();
        order.setAssetId(orderDto.getAssetId());
        order.setOrderSide(orderDto.getOrderSide().toString());
        order.setCurrency(orderDto.getCurrency());
        order.setPrice(orderDto.getPrice());
        order.setCustomerId(orderDto.getCustomerId());
        order.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        order.setStatus(OrderStatus.PENDING.toString());
        order.setSize(orderDto.getSize());

        Order saveOrder = orderRepository.save(order);
        orderDto.setId(saveOrder.getId());
        orderDto.setStatus(OrderStatus.valueOf(order.getStatus()));
        orderDto.setAsset(orderDto.getAsset());
        orderDto.setCustomer(order.getCustomer());

        asset.setUsableSize(asset.getUsableSize()-orderDto.getSize());
        assetRepository.save(asset);

        return orderDto;
    }
    private OrderDto sellAsset(OrderDto orderDto) {
        if (!Objects.isNull(orderDto.getId())) {
            throw new IllegalArgumentException("Order is should be new a request!! ");
        }
        if (!getUser().getUserType().equals(UserType.CUSTOMER.toString())) {
            throw new UserUnAuthorizedException(String.format("Sell for (CUSTOMER) UnAuthorized user type {0}",getUser().getUserType()));
        }
        if (!getUser().getDefaultCurrency().equals(orderDto.getCurrency())) {
            throw  new IllegalArgumentException("Order Currency can not match Customer Default Currency");
        }
        Asset asset = assetControl(orderDto.getAssetId()).orElseThrow(()->new IllegalArgumentException("Asset there is not found"));

        Order order = new Order();
        order.setAssetId(orderDto.getAssetId());
        order.setOrderSide(orderDto.getOrderSide().toString());
        order.setCurrency(orderDto.getCurrency());
        order.setPrice(orderDto.getPrice());
        order.setCustomerId(orderDto.getCustomerId());
        order.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        order.setStatus(OrderStatus.PENDING.toString());
        order.setSize(orderDto.getSize());

        Order saveOrder = orderRepository.save(order);
        orderDto.setId(saveOrder.getId());
        orderDto.setStatus(OrderStatus.valueOf(order.getStatus()));
        orderDto.setAsset(orderDto.getAsset());
        orderDto.setCustomer(order.getCustomer());


        asset.setUsableSize(asset.getUsableSize()+orderDto.getSize());
        if (!depositMoney(getUser().getIban(), orderDto.getPrice()))
            throw  new CustomerBalanceException(String.format(" Customer can not deposit iban {0} ",getUser().getIban()));
        assetRepository.save(asset);
        return  orderDto;

    }

    public OrderDto setMatched(Integer orderId) {
        if (Objects.isNull(orderId)) {
            throw new IllegalArgumentException("Order should has an Id!! ");
        }

        if (!getUser().getUserType().equals(UserType.ADMIN.toString())) {
            throw new UserUnAuthorizedException(String.format("Matched for (ADMIN)  UnAuthorized user type {0}",getUser().getUserType()));
        }

        Optional<Order> orderOptional = orderRepository.findById(orderId);
        Order order = orderOptional.orElseThrow(()->new NoSuchOrderExistsException("Order there is not found"));

        if (!order.getStatus().equals(OrderStatus.PENDING.toString())) {
            throw new IllegalArgumentException(String.format("Matched for  Order is should be status PENDING status {0}",order.getStatus()));
        }

        if (!depositMoney(getUser().getIban(),order.getPrice()))
            throw new CustomerBalanceException("Customer deposit money has a problem");

        order.setStatus(OrderStatus.MATCHED.toString());
        OrderDto orderDto = orderRepository.save(order).convertDto();
        newOrderTrade(orderDto);

        return orderDto;
    };
    public OrderDto setCanceled(Integer orderId) {
        if (Objects.isNull(orderId)) {
            throw new IllegalArgumentException("Order should has an Id!! ");
        }

        Optional<Order> orderOptional = orderRepository.findById(orderId);
        Order order = orderOptional.orElseThrow(()->new NoSuchOrderExistsException("Order there is not found"));
        if (!order.getStatus().equals(OrderStatus.PENDING.toString())) {
            throw new IllegalArgumentException(String.format("Canceled Order is should be status PENDING status {0}",order.getStatus()));
        }
        order.setStatus(OrderStatus.CANCELED.toString());
        if (!depositMoney(getUser().getIban(),order.getPrice()))
            throw new CustomerBalanceException("Customer deposit money has a problem");

       return orderRepository.save(order).convertDto();
    };

    private  void newOrderTrade(OrderDto orderDto) {
        OrderTrade orderTrade = new OrderTrade();
        orderTrade.setOrderId(orderDto.getId());
        orderTrade.setCustomerId(orderDto.getCustomerId());
        orderTrade.setAssetId(orderDto.getAssetId());
        orderTrade.setTradeTime(Timestamp.valueOf(LocalDateTime.now()));
        orderTrade.setPrice(orderDto.getPrice());
        orderTrade.setQuantity(orderDto.getSize());
        orderTrade.setMatchedAdminId(getUser().getId());
        orderTradeRepository.save(orderTrade);
    }

    public List<OrderDto> getOrderListByCustomerAndDateRange(Integer customerId, java.util.Date  beginDate,java.util.Date endDate) {
       Date beginDate_L = new Date(beginDate.getTime());
       Date endDate_L = new Date(endDate.getTime());
      return orderRepository.findAllByCustomerIdAndCreatedDateBetween(customerId,beginDate_L,endDate_L).stream().map(Order::convertDto).collect(Collectors.toList());
    }

    private boolean withDrawMoney(String iban,Double price) {
        String token  = "Bearer "+jwtService.generateToken(getUser());
        return customerBalanceClient.postWithDraw(iban,price,token);
    }
    private boolean depositMoney(String iban,Double price) {
        String token  = "Bearer "+jwtService.generateToken(getUser());
        return customerBalanceClient.postDeposit(iban,price,token);
    }

    private Optional<Asset>  assetControl(Integer assetId) {
            return assetRepository.findById(assetId);
    }


}

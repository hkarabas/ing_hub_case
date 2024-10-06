package com.ing_hub_case.services;

import com.ing_hub_case.entities.Order;
import com.ing_hub_case.entities.OrderTrade;
import com.ing_hub_case.entities.User;
import com.ing_hub_case.enums.OrderStatus;
import com.ing_hub_case.enums.UserType;
import com.ing_hub_case.exception.CustomerBalanceException;
import com.ing_hub_case.exception.NoSuchOrderExistsException;
import com.ing_hub_case.exception.UserUnAuthorizedException;
import com.ing_hub_case.feignclients.CustomerBalanceClient;
import com.ing_hub_case.models.OrderDto;
import com.ing_hub_case.repositories.AssetRepository;
import com.ing_hub_case.repositories.OrderRepository;
import com.ing_hub_case.repositories.OrderTradeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class MatchedOrderService extends AbstractOrder implements IOrder<OrderDto> {


    protected MatchedOrderService(OrderRepository orderRepository, AssetRepository assetRepository,
                                  CustomerBalanceClient customerBalanceClient, JwtService jwtService, OrderTradeRepository orderTradeRepository) {
        super(orderRepository, assetRepository, customerBalanceClient, jwtService, orderTradeRepository);
    }

    @Override
    public ResponseEntity<OrderDto> doAction(OrderDto element) {
          return   new ResponseEntity<>(doChange(element.getId()), HttpStatus.ACCEPTED);
    }


    private OrderDto doChange(Integer orderId) {
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
    }

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


}

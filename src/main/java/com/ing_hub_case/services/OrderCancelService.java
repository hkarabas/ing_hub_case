package com.ing_hub_case.services;

import com.ing_hub_case.entities.Order;
import com.ing_hub_case.enums.OrderStatus;
import com.ing_hub_case.exception.CustomerBalanceException;
import com.ing_hub_case.exception.NoSuchOrderExistsException;
import com.ing_hub_case.feignclients.CustomerBalanceClient;
import com.ing_hub_case.models.OrderDto;
import com.ing_hub_case.repositories.AssetRepository;
import com.ing_hub_case.repositories.OrderRepository;
import com.ing_hub_case.repositories.OrderTradeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class OrderCancelService extends AbstractOrder implements IOrder<OrderDto> {


    public OrderCancelService(OrderRepository orderRepository, AssetRepository assetRepository,
                              CustomerBalanceClient customerBalanceClient, JwtService jwtService,
                              OrderTradeRepository orderTradeRepository) {
        super(orderRepository, assetRepository, customerBalanceClient, jwtService, orderTradeRepository);
    }

    @Override
    public ResponseEntity<OrderDto> doAction(OrderDto element) {
        return new ResponseEntity<>(doChange(element.getId()), HttpStatus.ACCEPTED);
    }

    private OrderDto doChange(Integer orderId) {
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
    }
}

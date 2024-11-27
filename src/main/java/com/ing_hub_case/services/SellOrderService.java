package com.ing_hub_case.services;


import com.ing_hub_case.entities.Asset;
import com.ing_hub_case.entities.Order;
import com.ing_hub_case.entities.User;
import com.ing_hub_case.enums.OrderStatus;
import com.ing_hub_case.enums.UserType;
import com.ing_hub_case.exception.CustomerBalanceException;
import com.ing_hub_case.exception.UserUnAuthorizedException;
import com.ing_hub_case.feignclients.CustomerBalanceClient;
import com.ing_hub_case.models.OrderDto;
import com.ing_hub_case.repositories.AssetRepository;
import com.ing_hub_case.repositories.OrderRepository;
import com.ing_hub_case.repositories.OrderTradeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SellOrderService extends AbstractOrder implements IOrder<OrderDto> {

    private static final String ORDER_SHOULD_BE_NEW = "Order is should be new a request!!";
    private static final String UNAUTHORIZED_USER_TYPE = "Sell for (CUSTOMER) UnAuthorized user type %s";
    private static final String CURRENCY_MISMATCH = "Order Currency does not match Customer Default Currency";
    private static final String ASSET_NOT_FOUND = "Asset there is not found";
    private static final String DEPOSIT_FAILED = "Customer can not deposit iban %s";


    protected SellOrderService(OrderRepository orderRepository, AssetRepository assetRepository,
                               CustomerBalanceClient customerBalanceClient,
                               JwtService jwtService, OrderTradeRepository orderTradeRepository) {
        super(orderRepository, assetRepository, customerBalanceClient, jwtService, orderTradeRepository);
    }

    @Override
    public ResponseEntity<OrderDto> doAction(OrderDto orderDto) {
        if (orderDto.getId() != null) {
            throw new IllegalArgumentException(ORDER_SHOULD_BE_NEW);
        }
        if (!getUser().getUserType().equals(UserType.CUSTOMER.toString())) {
            throw new UserUnAuthorizedException(String.format(UNAUTHORIZED_USER_TYPE, getUser().getUserType()));
        }
        if (!getUser().getDefaultCurrency().equals(orderDto.getCurrency())) {
            throw new IllegalArgumentException(CURRENCY_MISMATCH);
        }
        Asset asset = assetControl(orderDto.getAssetId()).orElseThrow(() -> new IllegalArgumentException(ASSET_NOT_FOUND));

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

        asset.setUsableSize(asset.getUsableSize() + orderDto.getSize());
        if (!depositMoney(getUser().getIban(), orderDto.getPrice())) {
            throw new CustomerBalanceException(String.format(DEPOSIT_FAILED, getUser().getIban()));
        }
        assetRepository.save(asset);
        return new ResponseEntity<>(orderDto, HttpStatus.CREATED);
    }


    @Override
    protected Optional<Asset> assetControl(Integer assetId) {
        return super.assetControl(assetId);
    }
}

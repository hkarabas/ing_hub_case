package com.ing_hub_case.services;

import com.ing_hub_case.entities.Asset;
import com.ing_hub_case.entities.Order;
import com.ing_hub_case.entities.User;
import com.ing_hub_case.enums.OrderStatus;
import com.ing_hub_case.enums.UserType;
import com.ing_hub_case.exception.CustomerBalanceException;
import com.ing_hub_case.exception.NoSuchOrderExistsException;
import com.ing_hub_case.feignclients.CustomerBalanceClient;
import com.ing_hub_case.models.OrderDto;
import com.ing_hub_case.repositories.AssetRepository;
import com.ing_hub_case.repositories.OrderRepository;
import com.ing_hub_case.repositories.OrderTradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderCancelServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private CustomerBalanceClient customerBalanceClient;

    @Mock
    private JwtService jwtService;

    @Mock
    private OrderTradeRepository orderTradeRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    Authentication authentication;

    @InjectMocks
    private OrderCancelService orderCancelService;

    private OrderDto orderDto;
    private Order order;
    private User user;
    private Asset asset;
    @BeforeEach
    void setUp() {
        orderDto = new OrderDto();
        orderDto.setId(1);
        orderDto.setCustomerId(1);
        orderDto.setAssetId(1);
        orderDto.setPrice(100.0);
        orderDto.setSize(10);

        order = new Order();
        order.setId(1);
        order.setCustomerId(1);
        order.setAssetId(1);
        order.setPrice(100.0);
        order.setSize(10);
        order.setOrderSide("MATCH");
        order.setStatus(OrderStatus.PENDING.name());

        user = new User();
        user.setId(1);
        user.setUserType(UserType.ADMIN.toString());
        user.setIban("TR123456789");
    }

    @Test
    void doAction_withValidOrder_returnsAcceptedResponse() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(customerBalanceClient.postDeposit(anyString(), anyDouble(),anyString())).thenReturn(true);

        ResponseEntity<OrderDto> response = orderCancelService.doAction(orderDto);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(OrderStatus.CANCELED.toString(), response.getBody().getStatus().toString());
    }

    @Test
    void doAction_withNullOrderId_throwsIllegalArgumentException() {
        OrderDto orderDto = new OrderDto();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderCancelService.doAction(orderDto);
        });

        assertEquals("Order should has an Id!! ", exception.getMessage());
    }

    @Test
    void doAction_withNonExistentOrder_throwsNoSuchOrderExistsException() {

        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        NoSuchOrderExistsException exception = assertThrows(NoSuchOrderExistsException.class, () -> {
            orderCancelService.doAction(orderDto);
        });

        assertEquals("Order there is not found", exception.getMessage());
    }

    @Test
    void doAction_withNonPendingOrder_throwsIllegalArgumentException() {
        order.setStatus(OrderStatus.MATCHED.name());
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderCancelService.doAction(orderDto);
        });

        assertEquals("Canceled Order is should be status PENDING status {0}", exception.getMessage());
    }

    @Test
    void doAction_withDepositMoneyFailure_throwsCustomerBalanceException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(order));
        when(customerBalanceClient.postDeposit(anyString(), anyDouble(),anyString())).thenReturn(false);

        CustomerBalanceException exception = assertThrows(CustomerBalanceException.class, () -> {
            orderCancelService.doAction(orderDto);
        });

        assertEquals("Customer deposit money has a problem", exception.getMessage());
    }
}
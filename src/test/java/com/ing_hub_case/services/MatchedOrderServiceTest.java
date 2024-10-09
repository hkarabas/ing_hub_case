package com.ing_hub_case.services;

import static org.apache.coyote.http11.Constants.a;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MatchedOrderServiceTest {

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
    private MatchedOrderService matchedOrderService;



    private OrderDto orderDto;
    private Order order;
    private User user;

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
        order.setStatus(OrderStatus.PENDING.toString());

        user = new User();
        user.setId(1);
        user.setUserType(UserType.ADMIN.toString());
        user.setIban("TR123456789");
    }

    @Test
    void doAction_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(orderRepository.findById(orderDto.getId())).thenReturn(Optional.of(order));
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        when(customerBalanceClient.postDeposit(anyString(), anyDouble(),anyString())).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        ResponseEntity<OrderDto> response = matchedOrderService.doAction(orderDto);

        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(OrderStatus.MATCHED.toString(), response.getBody().getStatus().toString());

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderTradeRepository, times(1)).save(any(OrderTrade.class));
    }

    @Test
    void doAction_ThrowsException_WhenOrderIdIsNull() {
        orderDto.setId(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            matchedOrderService.doAction(orderDto);
        });

        assertEquals("Order should has an Id!! ", exception.getMessage());
    }

    @Test
    void doAction_ThrowsException_WhenUserIsNotAdmin() {
        user.setUserType(UserType.CUSTOMER.toString());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);
        UserUnAuthorizedException exception = assertThrows(UserUnAuthorizedException.class, () -> {
        matchedOrderService.doAction(orderDto);
        });

        assertEquals("Matched for (ADMIN)  UnAuthorized user type CUSTOMER", exception.getMessage());
    }

    @Test
    void doAction_ThrowsException_WhenOrderNotFound() {

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(orderRepository.findById(orderDto.getId())).thenReturn(Optional.empty());
        SecurityContextHolder.setContext(securityContext);



        NoSuchOrderExistsException exception = assertThrows(NoSuchOrderExistsException.class, () -> {
            matchedOrderService.doAction(orderDto);
        });

        assertEquals("Order there is not found", exception.getMessage());
    }

    @Test
    void doAction_ThrowsException_WhenOrderStatusIsNotPending() {
        order.setStatus(OrderStatus.MATCHED.toString());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);
        when(orderRepository.findById(orderDto.getId())).thenReturn(Optional.of(order));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            matchedOrderService.doAction(orderDto);
        });

        assertEquals("Matched for  Order is should be status PENDING status MATCHED", exception.getMessage());
    }

    @Test
    void doAction_ThrowsException_WhenDepositFails() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);
        when(orderRepository.findById(orderDto.getId())).thenReturn(Optional.of(order));


        CustomerBalanceException exception = assertThrows(CustomerBalanceException.class, () -> {
            matchedOrderService.doAction(orderDto);
        });

        assertEquals("Customer deposit money has a problem", exception.getMessage());
    }
}
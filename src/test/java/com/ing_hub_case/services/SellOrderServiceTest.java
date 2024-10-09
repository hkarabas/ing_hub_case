package com.ing_hub_case.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ing_hub_case.entities.Asset;
import com.ing_hub_case.entities.Order;
import com.ing_hub_case.entities.User;
import com.ing_hub_case.enums.Currency;
import com.ing_hub_case.enums.OrderSide;
import com.ing_hub_case.enums.OrderStatus;
import com.ing_hub_case.enums.UserType;
import com.ing_hub_case.exception.CustomerBalanceException;
import com.ing_hub_case.exception.NoSuchOrderExistsException;
import com.ing_hub_case.exception.UserUnAuthorizedException;
import com.ing_hub_case.feignclients.CustomerBalanceClient;
import com.ing_hub_case.models.OrderDto;
import com.ing_hub_case.repositories.AssetRepository;
import com.ing_hub_case.repositories.OrderRepository;
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
class SellOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private CustomerBalanceClient customerBalanceClient;

    @Mock
    private JwtService jwtService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    Authentication authentication;


    @InjectMocks
    private SellOrderService sellOrderService;

    private OrderDto orderDto;
    private Order order;
    private User user;
    private Asset asset;

    @BeforeEach
    void setUp() {
        orderDto = new OrderDto();
        orderDto.setCustomerId(1);
        orderDto.setAssetId(1);
        orderDto.setPrice(100.0);
        orderDto.setSize(10);
        orderDto.setCurrency(Currency.TRY.toString());
        orderDto.setOrderSide(OrderSide.SELL);

        order = new Order();
        order.setId(1);
        order.setCustomerId(1);
        order.setAssetId(1);
        order.setPrice(100.0);
        order.setSize(10);
        order.setCurrency(Currency.TRY.toString());
        order.setStatus(OrderStatus.PENDING.toString());

        user = new User();
        user.setId(1);
        user.setDefaultCurrency(Currency.TRY.toString());
        user.setUserType(UserType.CUSTOMER.toString());
        user.setIban("TR123456789");

        asset = new Asset();
        asset.setId(1);
        asset.setUsableSize(20L);
    }

    @Test
    void doAction_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);
        when(customerBalanceClient.postDeposit(anyString(), anyDouble(),anyString())).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(assetRepository.findById(anyInt())).thenReturn(Optional.of(asset));

        ResponseEntity<OrderDto> response = sellOrderService.doAction(orderDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(OrderStatus.PENDING.toString(), response.getBody().getStatus().toString());

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void doAction_ThrowsException_WhenOrderIdIsNull() {
        orderDto.setId(1);

        SecurityContextHolder.setContext(securityContext);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            sellOrderService.doAction(orderDto);
        });

        assertEquals("Order is should be new a request!!", exception.getMessage());
    }

    @Test
    void doAction_ThrowsException_WhenUserIsNotAdmin() {
        user.setUserType(UserType.ADMIN.toString());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);
        Order saveOrder = new  Order();
        saveOrder.setId(1);
        saveOrder.setStatus(OrderStatus.PENDING.toString());


        UserUnAuthorizedException exception = assertThrows(UserUnAuthorizedException.class, () -> {
            sellOrderService.doAction(orderDto);
        });

        assertEquals("Sell for (CUSTOMER) UnAuthorized user type ADMIN", exception.getMessage());
    }

    @Test
    void doAction_ThrowsException_WhenAssetNotFound() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            sellOrderService.doAction(orderDto);
        });

        assertEquals("Asset there is not found", exception.getMessage());
    }

    @Test
    void doAction_ThrowsException_WhenCustomerBalanceIsNotEnough() {
        order.setStatus(OrderStatus.MATCHED.toString());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);
        when(assetRepository.findById(anyInt())).thenReturn(Optional.of(asset));
        Order saveOrder = new  Order();

        saveOrder.setId(1);
        saveOrder.setStatus(OrderStatus.PENDING.toString());
        when(orderRepository.save(any(Order.class))).thenReturn(saveOrder);

        CustomerBalanceException exception = assertThrows(CustomerBalanceException.class, () -> {
            sellOrderService.doAction(orderDto);
        });

        assertEquals("Customer can not deposit iban TR123456789", exception.getMessage());
    }

    @Test
    void doAction_ThrowsException_WhenDepositFails() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);
        when(customerBalanceClient.postDeposit(anyString(), anyDouble(),anyString())).thenReturn(false);
        when(assetRepository.findById(anyInt())).thenReturn(Optional.of(asset));
        Order saveOrder = new  Order();
        saveOrder.setId(1);
        saveOrder.setStatus(OrderStatus.PENDING.toString());



        when(orderRepository.save(any(Order.class))).thenReturn(saveOrder);
        CustomerBalanceException exception = assertThrows(CustomerBalanceException.class, () -> {
            sellOrderService.doAction(orderDto);
        });

        assertEquals("Customer can not deposit iban TR123456789", exception.getMessage());
    }
}
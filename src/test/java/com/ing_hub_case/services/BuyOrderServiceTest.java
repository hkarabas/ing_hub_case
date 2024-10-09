package com.ing_hub_case.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ing_hub_case.entities.Asset;
import com.ing_hub_case.entities.Order;
import com.ing_hub_case.entities.User;
import com.ing_hub_case.enums.OrderSide;
import com.ing_hub_case.enums.OrderStatus;
import com.ing_hub_case.enums.UserType;
import com.ing_hub_case.exception.AssetUsableSizeException;
import com.ing_hub_case.exception.CustomerBalanceException;
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
public class BuyOrderServiceTest {

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
    SecurityContextHolder securityContextHolder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private BuyOrderService buyOrderService;

    private OrderDto orderDto;
    private User user;
    private Asset asset;

    @BeforeEach
    void setUp() {
        orderDto = new OrderDto();
        orderDto.setAssetId(1);
        orderDto.setOrderSide(OrderSide.BUY);
        orderDto.setCurrency("TRY");
        orderDto.setPrice(100.0);
        orderDto.setCustomerId(1);
        orderDto.setSize(10);

        user = new User();
        user.setEmail("hkarabas@gmail.com");
        user.setPassword("qweqeqweqwewqas45345345345");
        user.setFullName("hasan karabaş");
        user.setUserType("CUSTOMER");
        user.setDefaultCurrency("TRY");
        user.setIban("trreetetertettrtertert");




        asset = new Asset();
        asset.setUsableSize(20L);


    }

    @Test
    void testDoAction() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        when(assetRepository.findById(orderDto.getAssetId())).thenReturn(Optional.of(asset));
        when(customerBalanceClient.postWithDraw(anyString(), anyDouble(),anyString())).thenReturn(true);


        Order saveOrder = new  Order();
        saveOrder.setId(1);
        saveOrder.setStatus(OrderStatus.PENDING.toString());



        when(orderRepository.save(any(Order.class))).thenReturn(saveOrder);
        ResponseEntity<OrderDto> response = buyOrderService.doAction(orderDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(OrderStatus.PENDING, response.getBody().getStatus());

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(assetRepository, times(1)).save(any(Asset.class));
    }

    @Test
    void testDoAction_ThrowsException_WhenOrderIdIsNotNull() {
        orderDto.setId(1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            buyOrderService.doAction(orderDto);
        });

        assertEquals("Order is should be new a request!! ", exception.getMessage());
    }

    @Test
    void testDoAction_ThrowsException_WhenUserIsNotCustomer() {
        user.setUserType(UserType.ADMIN.toString());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);
        UserUnAuthorizedException exception = assertThrows(UserUnAuthorizedException.class, () -> {
            buyOrderService.doAction(orderDto);
        });

        assertEquals("Buy for (CUSTOMER) UnAuthorized user type {0}", exception.getMessage());
    }

    @Test
    void testDoAction_ThrowsException_WhenCurrencyDoesNotMatch() {
        orderDto.setCurrency("USD");
        user.setUserType(UserType.CUSTOMER.toString());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            buyOrderService.doAction(orderDto);
        });

        assertEquals("Order Currency can not match Customer Default Currency", exception.getMessage());
    }

    @Test
    void testDoAction_ThrowsException_WhenBalanceIsNotEnough() {
        when(customerBalanceClient.postWithDraw(anyString(), anyDouble(),anyString())).thenReturn(false);

        CustomerBalanceException exception = assertThrows(CustomerBalanceException.class, () -> {
            buyOrderService.doAction(orderDto);
        });

        assertEquals("Customer Amount Balance Enough", exception.getMessage());
    }

    @Test
    void testDoAction_ThrowsException_WhenAssetNotFound() {
        when(assetRepository.findById(orderDto.getAssetId())).thenReturn(Optional.empty());
        when(customerBalanceClient.postWithDraw(anyString(), anyDouble(),anyString())).thenReturn(Boolean.TRUE);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            buyOrderService.doAction(orderDto);
        });

        assertEquals("Asset there is not found", exception.getMessage());
    }

    @Test
    void testDoAction_ThrowsException_WhenUsableSizeIsNotEnough() {
        asset.setUsableSize(5L);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);
        when(customerBalanceClient.postWithDraw(anyString(), anyDouble(),anyString())).thenReturn(Boolean.TRUE);
        when(assetRepository.findById(orderDto.getAssetId())).thenReturn(Optional.of(asset));
        AssetUsableSizeException exception = assertThrows(AssetUsableSizeException.class, () -> {
            buyOrderService.doAction(orderDto);
        });

        assertEquals("Asset usable size not enough usable size {0}", exception.getMessage());
    }
}
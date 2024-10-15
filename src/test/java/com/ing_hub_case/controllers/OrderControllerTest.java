package com.ing_hub_case.controllers;

import com.ing_hub_case.entities.Asset;
import com.ing_hub_case.entities.Order;
import com.ing_hub_case.entities.OrderTrade;
import com.ing_hub_case.entities.User;
import com.ing_hub_case.enums.Currency;
import com.ing_hub_case.enums.OrderSide;
import com.ing_hub_case.enums.OrderStatus;
import com.ing_hub_case.enums.UserType;
import com.ing_hub_case.factorty.OrderFactory;
import com.ing_hub_case.feignclients.CustomerBalanceClient;
import com.ing_hub_case.models.OrderDto;
import com.ing_hub_case.repositories.AssetRepository;
import com.ing_hub_case.repositories.OrderRepository;
import com.ing_hub_case.services.AbstractOrder;
import com.ing_hub_case.services.AssetService;
import com.ing_hub_case.services.BuyOrderService;
import com.ing_hub_case.services.SellOrderService;
import jakarta.annotation.Resource;
import org.hibernate.mapping.Any;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SellOrderService sellOrderService;

    @MockBean
    OrderRepository orderRepository;

    @MockBean
    CustomerBalanceClient customerBalanceClient;

    @MockBean
    AssetRepository assetRepository;

    @Autowired
    private BuyOrderService buyOrderService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private OrderFactory orderFactory;

    private MockMvc mockMvc;

    @MockBean
    private SecurityContext securityContext;

    @MockBean
    Authentication authentication;

    private OrderDto orderDto;
    private Order order;
    private User user;

    private Asset asset;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        orderDto = new OrderDto();
        orderDto.setCustomerId(1);
        orderDto.setAssetId(1);
        orderDto.setPrice(100.0);
        orderDto.setSize(10);
        orderDto.setCurrency(Currency.TRY.toString());

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
    void doAction_withValidSellOrder_returnsCreatedResponse() throws Exception {

        orderDto.setOrderSide(OrderSide.SELL);
        orderDto.setStatus(OrderStatus.PENDING);

        OrderDto orderDtoRet = new OrderDto();
        orderDtoRet.setId(1);
        orderDtoRet.setOrderSide(OrderSide.SELL);
        orderDtoRet.setStatus(OrderStatus.PENDING);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);
        when(assetRepository.findById(1)).thenReturn(Optional.of(asset));
        Order saveOrder = new  Order();
        saveOrder.setId(1);
        saveOrder.setStatus(OrderStatus.PENDING.toString());
        saveOrder.setAssetId(1);
        asset.setUsableSize(asset.getUsableSize()-orderDto.getSize());
        when(orderRepository.save(any())).thenReturn(saveOrder);
        when(customerBalanceClient.postDeposit(anyString(), anyDouble(),anyString())).thenReturn(true);
        when(assetRepository.save(any())).thenReturn(asset);




        mockMvc.perform(post("/order/doAction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderSide\":\"SELL\",\"currency\":\"TRY\",\"price\":100.0,\"size\":10,\"assetId\":1,\"customerId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderSide").value("SELL"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void doAction_withValidBuyOrder_returnsCreatedResponse() throws Exception {

        orderDto.setOrderSide(OrderSide.BUY);
        orderDto.setStatus(OrderStatus.PENDING);

        OrderDto orderDtoRet = new OrderDto();
        orderDtoRet.setId(1);
        orderDtoRet.setOrderSide(OrderSide.BUY);
        orderDtoRet.setStatus(OrderStatus.PENDING);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);
        when(assetRepository.findById(1)).thenReturn(Optional.of(asset));
        Order saveOrder = new  Order();
        saveOrder.setId(1);
        saveOrder.setStatus(OrderStatus.PENDING.toString());
        saveOrder.setAssetId(1);
        asset.setUsableSize(asset.getUsableSize()-orderDto.getSize());
        when(orderRepository.save(any())).thenReturn(saveOrder);
        when(customerBalanceClient.postWithDraw(anyString(), anyDouble(),anyString())).thenReturn(true);
        when(assetRepository.save(any())).thenReturn(asset);


        mockMvc.perform(post("/order/doAction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderSide\":\"BUY\",\"currency\":\"TRY\",\"price\":100.0,\"size\":10,\"assetId\":1,\"customerId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderSide").value("BUY"))
                .andExpect(jsonPath("$.status").value("PENDING"));
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(assetRepository, times(1)).save(any(Asset.class));
        verify(customerBalanceClient, times(1)).postWithDraw(anyString(), anyDouble(),anyString());


    }

    @Test
    void doAction_withInvalidOrder_returnsBadRequest() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);
        orderDto.setId(1);


        mockMvc.perform(post("/order/doAction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"Id\":1,\"orderSide\":\"SELL\",\"currency\":\"USD\",\"price\":100.0,\"size\":10,\"assetId\":1,\"customerId\":1}"))
                .andExpect(status().isBadRequest());
    }
}
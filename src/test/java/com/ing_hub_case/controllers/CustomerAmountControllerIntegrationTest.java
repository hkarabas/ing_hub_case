package com.ing_hub_case.controllers;

import com.ing_hub_case.services.CustomerAmountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@AutoConfigureMockMvc(addFilters = false)
class CustomerAmountControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private CustomerAmountService customerAmountService;

    private MockMvc mockMvc;

    @Test
    void withDraw_withValidParameters_returnsAcceptedResponse() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        when(customerAmountService.withDrawMoney(anyString(), anyDouble())).thenReturn(true);

        mockMvc.perform(post("/customeramount/withdraw")
                        .param("iban", "IBAN123")
                        .param("price", "100.0"))
                .andExpect(status().isAccepted());
    }

    @Test
    void withDraw_withInvalidParameters_returnsAcceptedResponseWithFalse() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        when(customerAmountService.withDrawMoney(anyString(), anyDouble())).thenReturn(false);

        mockMvc.perform(post("/customeramount/withdraw")
                        .param("iban", "IBAN123")
                        .param("price", "100.0"))
                .andExpect(status().isAccepted());
    }

    @Test
    void depositDraw_withValidParameters_returnsAcceptedResponse() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        when(customerAmountService.depositMoney(anyString(), anyDouble())).thenReturn(true);

        mockMvc.perform(post("/customeramount/deposit")
                        .param("iban", "IBAN123")
                        .param("price", "100.0"))
                .andExpect(status().isAccepted());
    }

    @Test
    void depositDraw_withInvalidParameters_returnsAcceptedResponseWithFalse() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        when(customerAmountService.depositMoney(anyString(), anyDouble())).thenReturn(false);

        mockMvc.perform(post("/customeramount/deposit")
                        .param("iban", "IBAN123")
                        .param("price", "100.0"))
                .andExpect(status().isAccepted());
    }
}
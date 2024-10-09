package com.ing_hub_case.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ing_hub_case.entities.CustomerAmount;
import com.ing_hub_case.exception.NoSuchCustomerExistsException;
import com.ing_hub_case.repositories.CustomerAmountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CustomerAmountServiceTest {

    @Mock
    private CustomerAmountRepository customerAmountRepository;

    @InjectMocks
    private CustomerAmountService customerAmountService;

    private CustomerAmount customerAmount;

    @BeforeEach
    void setUp() {
        customerAmount = new CustomerAmount();
        customerAmount.setIban("TR123456789");
        customerAmount.setAmount(1000.0);
    }

    @Test
    void withDrawMoney_Success() {
        when(customerAmountRepository.findByIban("TR123456789")).thenReturn(Optional.of(customerAmount));

        Boolean result = customerAmountService.withDrawMoney("TR123456789", 500.0);

        assertTrue(result);
        assertEquals(500.0, customerAmount.getAmount());
        verify(customerAmountRepository, times(1)).save(customerAmount);
    }

    @Test
    void withDrawMoney_Failure_InsufficientFunds() {
        when(customerAmountRepository.findByIban("TR123456789")).thenReturn(Optional.of(customerAmount));

        Boolean result = customerAmountService.withDrawMoney("TR123456789", 1500.0);

        assertFalse(result);
        assertEquals(1000.0, customerAmount.getAmount());
        verify(customerAmountRepository, never()).save(customerAmount);
    }

    @Test
    void withDrawMoney_Failure_NoSuchCustomerExists() {
        when(customerAmountRepository.findByIban("TR123456789")).thenReturn(Optional.empty());

        NoSuchCustomerExistsException exception = assertThrows(NoSuchCustomerExistsException.class, () -> {
            customerAmountService.withDrawMoney("TR123456789", 500.0);
        });

        assertEquals("Iban account not found !!!", exception.getMessage());
        verify(customerAmountRepository, never()).save(any(CustomerAmount.class));
    }

    @Test
    void depositMoney_Success() {
        when(customerAmountRepository.findByIban("TR123456789")).thenReturn(Optional.of(customerAmount));

        Boolean result = customerAmountService.depositMoney("TR123456789", 500.0);

        assertTrue(result);
        assertEquals(1500.0, customerAmount.getAmount());
        verify(customerAmountRepository, times(1)).save(customerAmount);
    }

    @Test
    void depositMoney_Failure_NoSuchCustomerExists() {
        when(customerAmountRepository.findByIban("TR123456789")).thenReturn(Optional.empty());

        Boolean result = customerAmountService.depositMoney("TR123456789", 500.0);

        assertFalse(result);
        verify(customerAmountRepository, never()).save(any(CustomerAmount.class));
    }
}
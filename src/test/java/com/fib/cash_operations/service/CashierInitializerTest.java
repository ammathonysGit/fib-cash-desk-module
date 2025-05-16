package com.fib.cash_operations.service;

import com.fib.cash_operations.model.Cashier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CashierInitializerTest {

    @Mock
    private CashBalanceService cashBalanceService;

    @InjectMocks
    private CashierInitializer cashierInitializer;

    @BeforeEach
    void setUp() {
        List<String> mockCashiers = List.of("PETER", "ANNA");
        ReflectionTestUtils.setField(cashierInitializer, "configuredCashiers", mockCashiers);
    }

    @Test
    void testInitCashiers_shouldInitializeAllCashiersWithCorrectBalances() {
        cashierInitializer.initCashiers();

        Map<String, Cashier> cashiers = cashierInitializer.getCashiers();

        assertEquals(2, cashiers.size());

        Cashier peter = cashiers.get("PETER");
        assertNotNull(peter);
        assertEquals("PETER", peter.getName());
        assertEquals(1000, peter.getBalanceByCurrency("BGN").getTotalAmount());
        assertEquals(2000, peter.getBalanceByCurrency("EUR").getTotalAmount());

        Cashier anna = cashiers.get("ANNA");
        assertNotNull(anna);
        assertEquals("ANNA", anna.getName());

        verify(cashBalanceService, times(4))
                .updateBalanceAndDenominations(any(), anyInt(), any(), anyList());
    }
}
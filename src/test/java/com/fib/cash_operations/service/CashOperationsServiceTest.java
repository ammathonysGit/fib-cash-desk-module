package com.fib.cash_operations.service;

import com.fib.cash_operations.exception.InsufficientBalanceException;
import com.fib.cash_operations.model.Cashier;
import com.fib.cash_operations.model.CurrencyBalance;
import com.fib.cash_operations.model.Denomination;
import com.fib.cash_operations.request.CashOperationRequest;
import com.fib.cash_operations.util.SupportedCashOperations;
import com.fib.cash_operations.util.SupportedCurrencies;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CashOperationsServiceTest {

    @Mock
    private CashierInitializer cashierInitializer;

    @Mock
    private TransactionManagementService transactionManagementService;

    @Mock
    private CashBalanceService cashBalanceService;

    @InjectMocks
    private CashOperationsService cashOperationsService;

    private CurrencyBalance currencyBalance;

    @BeforeEach
    void setup() {
        Denomination denominations = new Denomination(10, 10);
        currencyBalance = new CurrencyBalance( 100, new ArrayList<>(List.of(denominations)));
        Cashier cashier = new Cashier("PETER");
        cashier.addCurrencyBalance(SupportedCurrencies.BGN, currencyBalance);
        when(cashierInitializer.getCashiers()).thenReturn(Map.of("PETER", cashier));
    }

    @Test
    void testDepositUpdatesBalanceAndWritesTransaction() {
        CashOperationRequest request = new CashOperationRequest();
        request.setCashier("PETER");
        request.setAmount(100);
        request.setCurrency(SupportedCurrencies.BGN);
        request.setOperationType(SupportedCashOperations.DEPOSIT);
        request.setDenominations(List.of(new Denomination(2, 50)));

        cashOperationsService.deposit(request);

        assertEquals(200, currencyBalance.getTotalAmount());
        assertTrue(currencyBalance.getDenominations().stream()
                .anyMatch(d -> d.getCashNote() == 50 && d.getAmount() == 2));
        verify(transactionManagementService).writeTransaction(any());
        verify(cashBalanceService).updateBalanceAndDenominations(eq("PETER"), eq(200), eq("BGN"), anyList());
    }

    @Test
    void testWithdrawSubtractsCorrectDenominationsAndUpdatesBalance() {
        CashOperationRequest request = new CashOperationRequest();
        request.setCashier("PETER");
        request.setAmount(50);
        request.setCurrency(SupportedCurrencies.BGN);
        request.setOperationType(SupportedCashOperations.WITHDRAW);

        cashOperationsService.withdraw(request);

        assertEquals(50, currencyBalance.getTotalAmount());
        assertTrue(currencyBalance.getDenominations().stream()
                .anyMatch(d -> d.getCashNote() == 10 && d.getAmount() == 5));
        verify(transactionManagementService).writeTransaction(any());
        verify(cashBalanceService).updateBalanceAndDenominations(eq("PETER"), eq(50), eq("BGN"), anyList());
    }

    @Test
    void testWithdrawThrowsExceptionOnInsufficientBalance() {
        CashOperationRequest request = new CashOperationRequest();
        request.setCashier("PETER");
        request.setAmount(150);
        request.setCurrency(SupportedCurrencies.BGN);
        request.setOperationType(SupportedCashOperations.WITHDRAW);

        InsufficientBalanceException ex = assertThrows(
                InsufficientBalanceException.class,
                () -> cashOperationsService.withdraw(request)
        );

        assertTrue(ex.getMessage().contains("Insufficient balance"));
    }
}
package com.fib.cash_operations.service;

import com.fib.cash_operations.request.CashOperationRequest;

public interface CashOperationsI {

    void deposit(CashOperationRequest cashOperationRequest);
    void withdraw(CashOperationRequest cashOperationRequest);
}

package com.fib.cash_operations.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BalanceOperationRequest {

    private LocalDate dateFrom;
    private LocalDate dateTo;

    @Pattern(regexp = "MARTINA|PETER|LINDA", message = "Unknown Cashier name provided")
    private String cashierName;

}

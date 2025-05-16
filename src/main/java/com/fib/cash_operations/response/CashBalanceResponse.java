package com.fib.cash_operations.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CashBalanceResponse {

    private String cashierName;
    private Integer currentBalance;
    private String denominations;
    private String currency;
    private LocalDate date;


}

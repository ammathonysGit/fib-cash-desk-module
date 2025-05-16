package com.fib.cash_operations.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class Balance {

    private CurrencyBalance currencyBalance;

    private int amount;

    private Map<String, List<Denomination>> currencyToDenominations;

}

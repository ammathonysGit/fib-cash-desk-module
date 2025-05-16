package com.fib.cash_operations.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class CurrencyBalance {

    private int totalAmount;

    private List<Denomination> denominations;

}

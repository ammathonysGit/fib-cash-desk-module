package com.fib.cash_operations.model;


import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Cashier {

    private final String name;
    private final Map<String, CurrencyBalance> balances = new HashMap<>();

    public Cashier(String name) {

        this.name = name;

    }

    public void addCurrencyBalance(String currency, CurrencyBalance currencyBalance) {
        balances.put(currency, currencyBalance);
    }

    public CurrencyBalance getBalanceByCurrency(String currency) {
        return balances.get(currency);
    }

}

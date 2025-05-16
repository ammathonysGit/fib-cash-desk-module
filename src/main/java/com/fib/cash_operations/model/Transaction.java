package com.fib.cash_operations.model;

import com.fib.cash_operations.util.DenominationsUtil;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class Transaction {

    private final String cashierName;
    private final String type;
    private final String currency;
    private final double amount;
    private final List<Denomination> denominations;
    private final LocalDate date;


    public Transaction(
            String cashierName,
            String type,
            String currency,
            double amount,
            List<Denomination> denominations
    ) {
        this.cashierName = cashierName;
        this.type = type;
        this.currency = currency;
        this.amount = amount;
        this.date = LocalDate.now();
        this.denominations = denominations;
    }

    @Override
    public String toString() {
        return date + " | " + cashierName + " | " + type + " | " + currency + " | " + amount + " | " + DenominationsUtil.formatDenominations(denominations);
    }

}

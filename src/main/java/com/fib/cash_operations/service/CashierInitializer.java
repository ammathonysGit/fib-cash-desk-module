package com.fib.cash_operations.service;

import com.fib.cash_operations.model.Cashier;
import com.fib.cash_operations.model.CurrencyBalance;
import com.fib.cash_operations.model.Denomination;
import com.fib.cash_operations.util.SupportedCurrencies;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Getter
@Slf4j
public class CashierInitializer {

    private final Map<String, Cashier> cashiers = new HashMap<>();

    @Value("#{'${fib.api.cashiers}'.split(',')}")
    private List<String> configuredCashiers;

    private final CashBalanceService cashBalanceService;

    public CashierInitializer(CashBalanceService cashBalanceService) {
        this.cashBalanceService = cashBalanceService;
    }

    @PostConstruct
    public void initCashiers() {
        log.info("Initializing Cashiers: {}", configuredCashiers);

        List<Denomination> defaultBGNDenominations = new ArrayList<>();
        defaultBGNDenominations.add(new Denomination(50, 10));
        defaultBGNDenominations.add(new Denomination(10, 50));

        List<Denomination> defaultEURDenominations = new ArrayList<>();
        defaultEURDenominations.add(new Denomination(100, 10));
        defaultEURDenominations.add(new Denomination(20, 50));

        for (String cashierName : configuredCashiers) {
            Cashier cashier = new Cashier(cashierName);

            List<Denomination> bgnDenominations = new ArrayList<>();
            for (Denomination bgnDenomination : defaultBGNDenominations) {
                bgnDenominations.add(new Denomination(bgnDenomination.getCashNote(), bgnDenomination.getAmount()));
            }

            List<Denomination> eurDenominations = new ArrayList<>();
            for (Denomination euroDenomination : defaultEURDenominations) {
                eurDenominations.add(new Denomination(euroDenomination.getCashNote(), euroDenomination.getAmount()));
            }

            CurrencyBalance bgnBalance = new CurrencyBalance(1000, bgnDenominations);
            CurrencyBalance eurBalance = new CurrencyBalance(2000, eurDenominations);

            cashier.addCurrencyBalance(SupportedCurrencies.BGN, bgnBalance);
            cashier.addCurrencyBalance(SupportedCurrencies.EUR, eurBalance);

            cashiers.put(cashierName, cashier);

            cashBalanceService.updateBalanceAndDenominations(
                    cashierName, bgnBalance.getTotalAmount(), SupportedCurrencies.BGN, bgnDenominations
            );
            cashBalanceService.updateBalanceAndDenominations(
                    cashierName, eurBalance.getTotalAmount(), SupportedCurrencies.EUR, eurDenominations
            );
        }
    }
}
package com.fib.cash_operations.service;

import com.fib.cash_operations.exception.InsufficientBalanceException;
import com.fib.cash_operations.model.*;
import com.fib.cash_operations.request.CashOperationRequest;
import com.fib.cash_operations.util.DenominationsUtil;
import com.fib.cash_operations.util.SupportedCashOperations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class CashOperationsService implements CashOperationsI {

    private final CashierInitializer cashierInitializer;
    private final TransactionManagementService transactionManagementService;
    private final CashBalanceService cashBalanceService;

    @Autowired
    public CashOperationsService(
            CashierInitializer cashierInitializer,
            TransactionManagementService transactionManagementService,
            CashBalanceService cashBalanceService

    ) {

        this.cashierInitializer = cashierInitializer;
        this.transactionManagementService = transactionManagementService;
        this.cashBalanceService = cashBalanceService;
    }

    @Override
    public void deposit(CashOperationRequest cashOperationRequest) {
        log.info("Performing DEPOSIT for Cashier= {} Amount= {} Currency= {} Denominations= {}",
                cashOperationRequest.getCashier(),
                cashOperationRequest.getAmount(),
                cashOperationRequest.getCurrency(),
                cashOperationRequest.getDenominations()
        );

        DenominationsUtil.validateDenominationsForDeposit(
                cashOperationRequest.getDenominations(),
                cashOperationRequest.getCurrency(),
                cashOperationRequest.getAmount()
        );

        Cashier cashier = cashierInitializer.getCashiers().get(cashOperationRequest.getCashier());

        CurrencyBalance currencyBalance = cashier.getBalanceByCurrency(cashOperationRequest.getCurrency());

        List<Denomination> requestDenominations = cashOperationRequest.getDenominations();

        for (Denomination requestDenomination : requestDenominations) {

            Denomination matchedDenomination = null;
            for (Denomination cashierDenomination : currencyBalance.getDenominations()) {
                if (Objects.equals(cashierDenomination.getCashNote(), requestDenomination.getCashNote())) {
                    matchedDenomination = cashierDenomination;
                    break;
                }
            }

            if (matchedDenomination != null) {
                matchedDenomination.setAmount(matchedDenomination.getAmount() + requestDenomination.getAmount());
            } else {
                currencyBalance.getDenominations().add(new Denomination(requestDenomination.getAmount(), requestDenomination.getCashNote()));
            }
        }
        currencyBalance.setTotalAmount(currencyBalance.getTotalAmount() + cashOperationRequest.getAmount());

        Transaction transaction = new Transaction(
                cashier.getName(),
                SupportedCashOperations.DEPOSIT,
                cashOperationRequest.getCurrency(),
                cashOperationRequest.getAmount(),
                requestDenominations
        );

        transactionManagementService.writeTransaction(transaction);

        cashBalanceService.updateBalanceAndDenominations(
                cashier.getName(),
                currencyBalance.getTotalAmount(),
                cashOperationRequest.getCurrency(),
                currencyBalance.getDenominations()
        );

    }

    @Override
    public void withdraw(CashOperationRequest cashOperationRequest) {

        log.info("Performing WITHDRAWAL for Cashier= {} Amount= {} Currency= {}",
                cashOperationRequest.getCashier(),
                cashOperationRequest.getAmount(),
                cashOperationRequest.getCurrency()
        );

        Cashier cashier = cashierInitializer.getCashiers().get(cashOperationRequest.getCashier());

        CurrencyBalance currencyBalance = cashier.getBalanceByCurrency(cashOperationRequest.getCurrency());

        if (currencyBalance.getTotalAmount() - cashOperationRequest.getAmount() < 0) {
            log.error("Error performing WITHDRAWAL for Cashier= {} Reason: Insufficient Balance= {}", cashier.getName(), currencyBalance.getTotalAmount());
            throw new InsufficientBalanceException(
                    String.format("Insufficient balance! Available balance: %s %s",
                            currencyBalance.getTotalAmount(),
                            cashOperationRequest.getCurrency())
            );
        }

        List<Denomination> calculatedDenominations = DenominationsUtil.calculateDenominations(
                cashOperationRequest.getAmount(),
                currencyBalance.getDenominations());

        for (Denomination usedDenomination : calculatedDenominations) {

            for (Denomination currencyDenomination : currencyBalance.getDenominations()) {

                if (usedDenomination.getCashNote() == currencyDenomination.getCashNote()) {

                    if (currencyDenomination.getAmount() < usedDenomination.getAmount()) {
                        throw new RuntimeException("Insufficient bills");
                    }
                    currencyDenomination.setAmount(currencyDenomination.getAmount() - usedDenomination.getAmount());
                }
            }
        }


        currencyBalance.setTotalAmount(currencyBalance.getTotalAmount() - cashOperationRequest.getAmount());

        Transaction transaction = new Transaction(
                cashier.getName(),
                SupportedCashOperations.WITHDRAW,
                cashOperationRequest.getCurrency(),
                cashOperationRequest.getAmount(),
                calculatedDenominations);

        transactionManagementService.writeTransaction(transaction);

        cashBalanceService.updateBalanceAndDenominations(
                cashier.getName(),
                currencyBalance.getTotalAmount(),
                cashOperationRequest.getCurrency(),
                currencyBalance.getDenominations()
        );

    }

}

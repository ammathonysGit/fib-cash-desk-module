package com.fib.cash_operations.service;

import com.fib.cash_operations.exception.FileOperationException;
import com.fib.cash_operations.model.Transaction;
import com.fib.cash_operations.util.DenominationsUtil;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

@Service
@Slf4j
public class TransactionManagementService {

    private static final String TRANSACTIONS_FILE = "transactions.txt";

    public void writeTransaction(Transaction transaction) {

        log.info("Creating Transaction: {}", transaction);

        try (FileWriter writer = new FileWriter(TRANSACTIONS_FILE, true)) {
            String row = transaction.getCashierName() + " | " +
                    LocalDate.now() + " | " +
                    transaction.getType() + " | " +
                    transaction.getCurrency() + " | " +
                    transaction.getAmount() + " | " +
                    DenominationsUtil.formatDenominations(transaction.getDenominations()) + "\n" ;

            writer.write(row);
        } catch (IOException e) {
            log.error("Error occurred while creating Transaction: {}", transaction);
            throw new FileOperationException("Failed to load Transaction data", e.getCause());
        }
    }

    @PreDestroy
    public void clearTransactionsContents() {
        try (FileWriter writer = new FileWriter(TRANSACTIONS_FILE, false)) {
            writer.write("");
            log.info("Cleared contents of Transactions: {}", TRANSACTIONS_FILE);
        } catch (IOException e) {
            log.error("Failed to clear Transactions file: {}", TRANSACTIONS_FILE, e);
        }
    }
}

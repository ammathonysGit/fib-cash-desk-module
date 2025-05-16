package com.fib.cash_operations.service;

import com.fib.cash_operations.exception.FileOperationException;
import com.fib.cash_operations.model.Denomination;
import com.fib.cash_operations.request.BalanceOperationRequest;
import com.fib.cash_operations.response.CashBalanceResponse;
import com.fib.cash_operations.util.DenominationsUtil;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CashBalanceService {

    private static final String CASH_BALANCE_FILE = "cash_balance.txt";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void updateBalanceAndDenominations(
            String cashierName,
            Integer currentBalanceAmount,
            String currency,
            List<Denomination> denominations
    ) {
        log.info("Storing Balance and Denominations for Cashier= {} CurrentBalance= {} Currency= {}", cashierName, currentBalanceAmount, currency);

        try (FileWriter writer = new FileWriter(CASH_BALANCE_FILE, true)) {
            String row = cashierName + " | " +
                    LocalDate.now() + " | " +
                    currentBalanceAmount + " | " +
                    currency + " | " +
                    DenominationsUtil.formatDenominations(denominations) + "\n";

            writer.write(row);
        } catch (IOException e) {
            log.error("Error occurred while saving Balance data for Cashier= {}", cashierName);
            throw new FileOperationException("Failed to save Balance data", e.getCause());
        }
    }

    public List<CashBalanceResponse> retrieveBalanceAndDenominations(BalanceOperationRequest request) {

        log.info("Fetching Balance and Denominations data for Request= {}", request);

        List<CashBalanceResponse> responses = new ArrayList<>();

        LocalDate dateFrom = request.getDateFrom();
        LocalDate dateTo = request.getDateTo();

        if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
            log.warn("Invalid date range for Request= {}", request);
            throw new IllegalArgumentException(String.format(
                    "Invalid date range: dateFrom (%s) cannot be after 'dateTo' (%s)",
                    dateFrom, dateTo
            ));        }

        try (BufferedReader reader = new BufferedReader(new FileReader(CASH_BALANCE_FILE))) {
            String row;

            while ((row = reader.readLine()) != null) {
                String[] fields = row.split(" \\| ");
                if (fields.length < 5) continue;

                String cashierName = fields[0];
                LocalDate date = LocalDate.parse(fields[1], DATE_FORMAT);
                int currentBalance = Integer.parseInt(fields[2]);
                String currency = fields[3];
                String denominations = fields[4];

                if (dateFrom != null && date.isBefore(dateFrom)) continue;
                if (dateTo != null && date.isAfter(dateTo)) continue;
                if (request.getCashierName() != null && !cashierName.equals(request.getCashierName())) continue;

                responses.add(CashBalanceResponse.builder()
                        .cashierName(cashierName)
                        .currentBalance(currentBalance)
                        .denominations(denominations)
                        .currency(currency)
                        .date(date)
                        .build());
            }
        } catch (IOException e) {
            log.error("Error occurred while trying to fetch Balance data for Request= {}", request);
            throw new FileOperationException("Failed to load Balance data", e.getCause());
        }

        log.info("Successfully processed Balance= {}", responses);

        return responses;
    }

    @PreDestroy
    public void clearBalanceContents() {
        try (FileWriter writer = new FileWriter(CASH_BALANCE_FILE, false)) {
            writer.write("");
            log.info("Cleared contents of file: {}", CASH_BALANCE_FILE);
        } catch (IOException e) {
            log.error("Failed to clear file contents: {}", CASH_BALANCE_FILE, e);
        }
    }

}
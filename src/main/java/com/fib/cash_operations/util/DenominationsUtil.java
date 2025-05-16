package com.fib.cash_operations.util;


import com.fib.cash_operations.exception.DenominationValidationException;
import com.fib.cash_operations.model.Denomination;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static com.fib.cash_operations.util.AvailableCashNotes.validCashNotesBGN;
import static com.fib.cash_operations.util.AvailableCashNotes.validCashNotesEUR;

@Slf4j
public class DenominationsUtil {

    public static List<Denomination> calculateDenominations(
            int amount,
            List<Denomination> currentBalanceDenominations
    ) {
        log.info("Calculating Denominations for Amount= {} and CurrentDenominations= {}", amount, currentBalanceDenominations);

        List<Denomination> sortedDenominations = new ArrayList<>(currentBalanceDenominations);
        sortedDenominations.sort(Comparator.comparingInt(Denomination::getCashNote).reversed());

        List<Denomination> result = new ArrayList<>();
        int remaining = amount;

        for (Denomination denomination : sortedDenominations) {
            int toUse = Math.min(remaining / denomination.getCashNote(), denomination.getAmount());
            if (toUse > 0) {
                result.add(new Denomination(toUse, denomination.getCashNote()));
                remaining -= toUse * denomination.getCashNote();
            }
        }

        if (remaining > 0) {
            log.error("Error during calculating Denominations");
            System.out.printf("WARNING: Cannot fully calculate currentBalanceDenominations for amount %d. %d remaining.%n", amount, remaining);
        }
        log.info("Denominations calculation completed with: Denominations= {}", result);

        return result;
    }

    public static void validateDenominationsForDeposit(List<Denomination> denominations, String currency, int amount) {

        if (denominations == null || denominations.isEmpty()) {
            log.error("Denomination Validation Error: No Denominations provided for DEPOSIT");
            throw new DenominationValidationException("Denominations must be provided when operation type is DEPOSIT");
        }

        Set<Integer> validCashNotes = SupportedCurrencies.BGN.equalsIgnoreCase(currency) ? validCashNotesBGN : validCashNotesEUR;

        int totalAmount = 0;
        for (Denomination denomination : denominations) {
            if (!validCashNotes.contains(denomination.getCashNote())) {
                log.error("Invalid cash note detected: {} for Currency: {}", denomination.getCashNote(), currency);
                throw new DenominationValidationException(String.format("Incorrect cash note provided: %s for Currency: %s", denomination.getCashNote(), currency));
            }
            totalAmount += denomination.getCashNote() * denomination.getAmount();
        }

        if (totalAmount != amount) {
            log.error("Mismatch: Expected amount: {} but calculated total is: {}", amount, totalAmount);
            throw new DenominationValidationException(String.format("Mismatch found between Provided Amount: %s and Calculated Total of Denominations: %s", amount, totalAmount));
        }
    }

    public static String formatDenominations(List<Denomination> denominations) {
        StringBuilder result = new StringBuilder();

        for (Denomination denomination : denominations) {
            if (!result.isEmpty()) {
                result.append(",");
            }
            result.append(denomination.getCashNote()).append("x").append(denomination.getAmount());
        }

        return result.toString();
    }
}

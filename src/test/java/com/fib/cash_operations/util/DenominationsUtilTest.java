package com.fib.cash_operations.util;

import com.fib.cash_operations.exception.DenominationValidationException;
import com.fib.cash_operations.model.Denomination;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DenominationsUtilTest {

    @Test
    void testCalculateDenominations_exactMatch() {
        List<Denomination> current = List.of(
                new Denomination(10, 10),
                new Denomination(5, 50)
        );

        List<Denomination> result = DenominationsUtil.calculateDenominations(100, current);

        assertEquals(1, result.size());
        assertEquals(new Denomination(2, 50), result.get(0));
    }

    @Test
    void testCalculateDenominations_insufficientFunds() {
        List<Denomination> current = List.of(
                new Denomination(1, 10),
                new Denomination(1, 5)
        );

        List<Denomination> result = DenominationsUtil.calculateDenominations(100, current);

        assertEquals(2, result.size());
        assertEquals(new Denomination(1, 10), result.get(0));
        assertEquals(new Denomination(1, 5), result.get(1));
    }

    @Test
    void testValidateDenominationsForDeposit_validInput() {
        List<Denomination> deposit = List.of(
                new Denomination(2, 10),
                new Denomination(2, 20)
        );

        assertDoesNotThrow(() -> DenominationsUtil.validateDenominationsForDeposit(deposit, "BGN", 60));
    }

    @Test
    void testValidateDenominationsForDeposit_nullList() {
        Exception exception = assertThrows(DenominationValidationException.class,
                () -> DenominationsUtil.validateDenominationsForDeposit(null, "BGN", 100));

        assertTrue(exception.getMessage().contains("Denominations must be provided"));
    }

    @Test
    void testValidateDenominationsForDeposit_invalidCashNote() {
        List<Denomination> deposit = List.of(new Denomination(999, 1));

        Exception exception = assertThrows(DenominationValidationException.class,
                () -> DenominationsUtil.validateDenominationsForDeposit(deposit, "BGN", 999));

        assertTrue(exception.getMessage().contains("Incorrect cash note"));
    }

    @Test
    void testValidateDenominationsForDeposit_mismatchedTotal() {
        List<Denomination> deposit = List.of(
                new Denomination(1, 10),
                new Denomination(1, 20)
        );

        Exception exception = assertThrows(DenominationValidationException.class,
                () -> DenominationsUtil.validateDenominationsForDeposit(deposit, "BGN", 100));

        assertTrue(exception.getMessage().contains("Mismatch found"));
    }

    @Test
    void testFormatDenominations() {
        List<Denomination> denominations = List.of(
                new Denomination(2, 50),
                new Denomination(1, 10)
        );

        String formatted = DenominationsUtil.formatDenominations(denominations);
        assertEquals("50x2,10x1", formatted);
    }

    @Test
    void testFormatDenominations_emptyList() {
        String formatted = DenominationsUtil.formatDenominations(List.of());
        assertEquals("", formatted);
    }
}
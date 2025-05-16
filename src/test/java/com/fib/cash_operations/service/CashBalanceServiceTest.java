package com.fib.cash_operations.service;

import com.fib.cash_operations.model.Denomination;
import com.fib.cash_operations.request.BalanceOperationRequest;
import com.fib.cash_operations.response.CashBalanceResponse;
import org.junit.jupiter.api.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CashBalanceServiceTest {

    private static final String CASH_BALANCE_FILE = "cash_balance.txt";
    private CashBalanceService cashBalanceService;

    @BeforeEach
    void setUp() throws IOException {
        cashBalanceService = new CashBalanceService();
        Path path = Paths.get(CASH_BALANCE_FILE);
        Files.deleteIfExists(path);
        Files.createFile(path);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(CASH_BALANCE_FILE));
    }

    @Test
    public void testUpdateBalanceAndDenominations_writesToFile() throws IOException {
        var denominations = List.of(new Denomination(2, 50), new Denomination(5, 10));
        cashBalanceService.updateBalanceAndDenominations("MARTINA", 150, "BGN", denominations);

        List<String> lines = Files.readAllLines(Paths.get(CASH_BALANCE_FILE));
        assertEquals(1, lines.size());
        String line = lines.get(0);
        assertTrue(line.contains("MARTINA"));
        assertTrue(line.contains("150"));
        assertTrue(line.contains("BGN"));
        assertTrue(line.contains("50x2"));
        assertTrue(line.contains("10x5"));
    }

    @Test
    public void testRetrieveBalanceAndDenominations_filtersCorrectly() throws IOException {
        String today = LocalDate.now().toString();
        String yesterday = LocalDate.now().minusDays(1).toString();

        try (FileWriter writer = new FileWriter(CASH_BALANCE_FILE, true)) {
            writer.write("MARTINA | " + yesterday + " | 200 | BGN | 50x4\n");
            writer.write("PETER | " + today + " | 300 | EUR | 100x3\n");
        }

        var request = new BalanceOperationRequest();
        request.setDateFrom(LocalDate.now().minusDays(1));
        request.setDateTo(LocalDate.now());
        request.setCashierName("PETER");

        List<CashBalanceResponse> responses = cashBalanceService.retrieveBalanceAndDenominations(request);

        assertEquals(1, responses.size());
        CashBalanceResponse response = responses.get(0);
        assertEquals("PETER", response.getCashierName());
        assertEquals(300, response.getCurrentBalance());
        assertEquals("EUR", response.getCurrency());
        assertEquals("100x3", response.getDenominations());
    }

    @Test
    public void testRetrieveBalanceAndDenominations_invalidDateRange_throwsException() {
        var request = new BalanceOperationRequest();
        request.setDateFrom(LocalDate.now());
        request.setDateTo(LocalDate.now().minusDays(1));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            cashBalanceService.retrieveBalanceAndDenominations(request);
        });

        assertTrue(thrown.getMessage().contains("dateFrom"));
        assertTrue(thrown.getMessage().contains("dateTo"));
    }

    @Test
    public void testClearBalanceContents_clearsFile() throws IOException {

        try (FileWriter writer = new FileWriter(CASH_BALANCE_FILE, true)) {
            writer.write("Some content\n");
        }

        cashBalanceService.clearBalanceContents();

        List<String> lines = Files.readAllLines(Paths.get(CASH_BALANCE_FILE));
        assertTrue(lines.isEmpty() || (lines.size() == 1 && lines.get(0).isEmpty()));
    }

}
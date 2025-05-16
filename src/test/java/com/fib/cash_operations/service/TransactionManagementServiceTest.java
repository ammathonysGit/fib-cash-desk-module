package com.fib.cash_operations.service;

import com.fib.cash_operations.exception.FileOperationException;
import com.fib.cash_operations.model.Denomination;
import com.fib.cash_operations.model.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionManagementServiceTest {

    private static final String TRANSACTIONS_FILE = "transactions.txt";
    private TransactionManagementService service;

    @BeforeEach
    void setUp() throws IOException {
        service = new TransactionManagementService();
        Files.deleteIfExists(Paths.get(TRANSACTIONS_FILE));
        Files.createFile(Paths.get(TRANSACTIONS_FILE));
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(TRANSACTIONS_FILE));
    }

    @Test
    void testWriteTransaction_AppendsTransactionToFile() throws IOException {
        Transaction transaction = new Transaction("PETER", "DEPOSIT", "BGN", 100,
                List.of(new Denomination(5, 10), new Denomination(1, 50)));

        service.writeTransaction(transaction);

        List<String> lines = Files.readAllLines(Paths.get(TRANSACTIONS_FILE));
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains("PETER"));
        assertTrue(lines.get(0).contains("DEPOSIT"));
        assertTrue(lines.get(0).contains("BGN"));
        assertTrue(lines.get(0).contains("100"));
        assertTrue(lines.get(0).contains("10x5"));
    }

    @Test
    void testClearTransactionsContents_EmptiesFile() throws IOException {
        Path path = Paths.get(TRANSACTIONS_FILE);
        Files.write(path, "some text".getBytes());

        service.clearTransactionsContents();

        String content = Files.readString(path);
        assertEquals("", content);
    }

    @Test
    void testWriteTransaction_ThrowsFileOperationExceptionOnError() {
        Transaction transaction = new Transaction("LINDA", "WITHDRAWAL", "EUR", 50,
                List.of(new Denomination(2, 20)));

        File file = new File(TRANSACTIONS_FILE);
        file.setReadOnly();

        FileOperationException exception = assertThrows(FileOperationException.class, () -> {
            service.writeTransaction(transaction);
        });

        assertTrue(exception.getMessage().contains("Failed to load Transaction data"));

    }
}
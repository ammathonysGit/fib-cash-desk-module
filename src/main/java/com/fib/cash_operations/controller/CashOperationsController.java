package com.fib.cash_operations.controller;

import com.fib.cash_operations.request.CashOperationRequest;
import com.fib.cash_operations.service.CashOperationsI;
import com.fib.cash_operations.util.SupportedCashOperations;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@Slf4j
public class CashOperationsController {

    @Autowired
    private final CashOperationsI cashOperations;

    @PostMapping(value = "/cash-operation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processTransaction(@Valid @RequestBody CashOperationRequest cashOperationRequest) {
        log.info("Processing cash operation : Request: {}", cashOperationRequest);

        switch (cashOperationRequest.getOperationType()) {
            case SupportedCashOperations.WITHDRAW -> {
                cashOperations.withdraw(cashOperationRequest);

                log.info("Successfully completed WITHDRAW operation for Request: {}", cashOperationRequest);
                return ResponseEntity.ok(Map.of("message", String.format("Successfully Withdrawal of %s %s", cashOperationRequest.getAmount(), cashOperationRequest.getCurrency())));
            }

            case SupportedCashOperations.DEPOSIT -> {
                cashOperations.deposit(cashOperationRequest);

                log.info("Successfully completed DEPOSIT operation for Request: {}", cashOperationRequest);
                return ResponseEntity.ok(Map.of(
                        "message",
                        String.format(
                                "Successful Deposit of %s %s",
                                cashOperationRequest.getAmount(),
                                cashOperationRequest.getCurrency()
                        )));
            }

            default -> {
                log.error("Unsupported Operation Type provided in Request: {}", cashOperationRequest);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Unsupported operation"));
            }
        }

    }
}
package com.fib.cash_operations.controller;

import com.fib.cash_operations.request.BalanceOperationRequest;
import com.fib.cash_operations.response.CashBalanceResponse;
import com.fib.cash_operations.service.CashBalanceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@Slf4j
public class CashBalanceController {

    @Autowired
    private final CashBalanceService cashBalanceService;

    @GetMapping(value = "/cash-balance", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CashBalanceResponse> fetchCashBalance(@Valid @ModelAttribute BalanceOperationRequest balanceOperationRequest) {
        log.info("Processing Cash Balance check for Request={}", balanceOperationRequest);
        return cashBalanceService.retrieveBalanceAndDenominations(balanceOperationRequest);
    }
}

package com.fib.cash_operations.request;

import com.fib.cash_operations.model.Denomination;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class CashOperationRequest {

    @NotBlank(message = "Operation type must not be empty")
    @Pattern(regexp = "DEPOSIT|WITHDRAWAL", message = "Unsupported Operation type, available: 'DEPOSIT' or 'WITHDRAWAL'")
    private String operationType;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be at least 1")
    private Integer amount;

    @NotBlank(message = "Currency must be provided")
    @Pattern(regexp = "BGN|EUR", message = "Invalid currency provided, supported currencies: 'BGN' or 'EUR'")
    private String currency;

    @NotBlank(message = "Cashier name must not be empty")
    @Pattern(regexp = "MARTINA|PETER|LINDA", message = "Unknown Cashier name provided")
    private String cashier;

    private List<Denomination> denominations;
}

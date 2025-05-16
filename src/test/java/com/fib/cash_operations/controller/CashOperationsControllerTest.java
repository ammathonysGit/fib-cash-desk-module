package com.fib.cash_operations.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fib.cash_operations.model.Denomination;
import com.fib.cash_operations.request.CashOperationRequest;
import com.fib.cash_operations.service.CashOperationsI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "FIB_API_KEY=f9Uie8nNf112hx8s")
public class CashOperationsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CashOperationsI cashOperations;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testDepositOperation_Success() throws Exception {
        CashOperationRequest request = new CashOperationRequest();
        request.setCashier("PETER");
        request.setAmount(100);
        request.setCurrency("BGN");
        request.setOperationType("DEPOSIT");
        request.setDenominations(List.of(new Denomination(10, 10)));

        mockMvc.perform(post("/api/v1/cash-operation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("FIB-X-AUTH", "f9Uie8nNf112hx8s"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successful Deposit of 100 BGN"));

        verify(cashOperations).deposit(request);
    }

    @Test
    void testWithdrawOperation_Success() throws Exception {
        CashOperationRequest request = new CashOperationRequest();
        request.setCashier("PETER");
        request.setAmount(50);
        request.setCurrency("EUR");
        request.setOperationType("WITHDRAWAL");

        mockMvc.perform(post("/api/v1/cash-operation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("FIB-X-AUTH", "f9Uie8nNf112hx8s"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully Withdrawal of 50 EUR"));

        verify(cashOperations).withdraw(request);
    }

    @Test
    void testUnsupportedOperation_ReturnsBadRequest_WhenOperationIsNotSupported() throws Exception {
        CashOperationRequest request = new CashOperationRequest();
        request.setCashier("LINDA");
        request.setAmount(200);
        request.setCurrency("BGN");
        request.setOperationType("UNKNOWN");
        request.setDenominations(List.of(new Denomination(10, 20)));

        mockMvc.perform(post("/api/v1/cash-operation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("FIB-X-AUTH", "f9Uie8nNf112hx8s"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.operationType").value("Unsupported Operation type, available: 'DEPOSIT' or 'WITHDRAWAL'"));

        verifyNoInteractions(cashOperations);
    }

    @Test
    void testMissingAmount_ReturnsValidationError() throws Exception {
        CashOperationRequest request = new CashOperationRequest();
        request.setCashier("LINDA");
        request.setCurrency("BGN");
        request.setOperationType("DEPOSIT");
        request.setDenominations(List.of(new Denomination(10, 10)));

        mockMvc.perform(post("/api/v1/cash-operation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("FIB-X-AUTH", "f9Uie8nNf112hx8s"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testOperation_withNoAuthToken_ExpectUnauthorized() throws Exception {
        CashOperationRequest request = new CashOperationRequest();
        request.setCashier("PETER");
        request.setAmount(100);
        request.setCurrency("BGN");
        request.setOperationType("DEPOSIT");
        request.setDenominations(List.of(new Denomination(10, 10)));

        mockMvc.perform(post("/api/v1/cash-operation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}

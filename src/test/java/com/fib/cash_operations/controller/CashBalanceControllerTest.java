package com.fib.cash_operations.controller;

import com.fib.cash_operations.response.CashBalanceResponse;
import com.fib.cash_operations.service.CashBalanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CashBalanceController.class)
@TestPropertySource(properties = "FIB_API_KEY=f9Uie8nNf112hx8s")
public class CashBalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CashBalanceService cashBalanceService;

    @Test
    public void testFetchCashBalance() throws Exception {
        List<CashBalanceResponse> mockResponse = List.of(
                CashBalanceResponse.builder()
                        .cashierName("PETER")
                        .currentBalance(500)
                        .denominations("10x50, 5x100")
                        .currency("BGN")
                        .date(LocalDate.of(2024, 5, 10))
                        .build()
        );

        when(cashBalanceService.retrieveBalanceAndDenominations(any()))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/cash-balance")
                        .param("dateFrom", "2024-05-01")
                        .param("dateTo", "2024-05-10")
                        .param("cashierName", "PETER")
                        .header("FIB-X-AUTH", "f9Uie8nNf112hx8s")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cashierName").value("PETER"))
                .andExpect(jsonPath("$[0].currentBalance").value(500))
                .andExpect(jsonPath("$[0].denominations").value("10x50, 5x100"))
                .andExpect(jsonPath("$[0].currency").value("BGN"))
                .andExpect(jsonPath("$[0].date").value("2024-05-10"));
    }

    @Test
    public void testFetchCashBalance_MissingAuthHeader_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/cash-balance")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testFetchCashBalance_MultipleCashiers() throws Exception {
        List<CashBalanceResponse> mockResponse = List.of(
                CashBalanceResponse.builder()
                        .cashierName("PETER")
                        .currentBalance(500)
                        .denominations("10x50, 5x100")
                        .currency("BGN")
                        .date(LocalDate.of(2025, 5, 10))
                        .build(),
                CashBalanceResponse.builder()
                        .cashierName("LINDA")
                        .currentBalance(1000)
                        .denominations("20x50, 10x100")
                        .currency("EUR")
                        .date(LocalDate.of(2025, 5, 10))
                        .build()
        );

        when(cashBalanceService.retrieveBalanceAndDenominations(any()))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/cash-balance")
                        .param("dateFrom", "2025-05-01")
                        .param("dateTo", "2024-05-10")
                        .header("FIB-X-AUTH", "f9Uie8nNf112hx8s")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.cashierName == 'PETER')]").exists())
                .andExpect(jsonPath("$[?(@.cashierName == 'LINDA')]").exists());
    }

    @Test
    public void testFetchCashBalance_NoResults() throws Exception {
        when(cashBalanceService.retrieveBalanceAndDenominations(any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/cash-balance")
                        .param("cashierName", "PETER")
                        .param("dateFrom", "2023-01-01")
                        .param("dateTo", "2023-01-02")
                        .header("FIB-X-AUTH", "f9Uie8nNf112hx8s")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testFetchCashBalance_MatchCashierByNameAndRanges() throws Exception {
        List<CashBalanceResponse> allPeterResponses = List.of(
                CashBalanceResponse.builder()
                        .cashierName("PETER")
                        .currency("BGN")
                        .currentBalance(1000)
                        .denominations("10x50")
                        .date(LocalDate.of(2024, 5, 1))
                        .build(),

                CashBalanceResponse.builder()
                        .cashierName("PETER")
                        .currency("BGN")
                        .currentBalance(900)
                        .denominations("5x100")
                        .date(LocalDate.of(2024, 5, 5))
                        .build(),

                CashBalanceResponse.builder()
                        .cashierName("PETER")
                        .currency("BGN")
                        .currentBalance(800)
                        .denominations("20x25")
                        .date(LocalDate.of(2024, 5, 10))
                        .build(),

                CashBalanceResponse.builder()
                        .cashierName("PETER")
                        .currency("BGN")
                        .currentBalance(700)
                        .denominations("1x700")
                        .date(LocalDate.of(2024, 4, 30))
                        .build()
        );

        List<CashBalanceResponse> expectedFiltered = allPeterResponses.stream()
                .filter(resp -> !resp.getDate().isBefore(LocalDate.of(2024, 5, 1)) &&
                        !resp.getDate().isAfter(LocalDate.of(2024, 5, 10)))
                .collect(Collectors.toList());

        when(cashBalanceService.retrieveBalanceAndDenominations(any()))
                .thenReturn(expectedFiltered);

        mockMvc.perform(get("/api/v1/cash-balance")
                        .param("cashierName", "PETER")
                        .param("dateFrom", "2024-05-01")
                        .param("dateTo", "2024-05-10")
                        .header("FIB-X-AUTH", "f9Uie8nNf112hx8s")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].cashierName").value("PETER"))
                .andExpect(jsonPath("$[1].cashierName").value("PETER"))
                .andExpect(jsonPath("$[2].cashierName").value("PETER"))
                .andExpect(jsonPath("$[0].date").value("2024-05-01"))
                .andExpect(jsonPath("$[1].date").value("2024-05-05"))
                .andExpect(jsonPath("$[2].date").value("2024-05-10"));
    }

}
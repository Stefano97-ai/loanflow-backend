package com.loanflow.controller;
import com.loanflow.dto.request.SimulatorRequest;
import com.loanflow.dto.response.SimulatorResponse;
import com.loanflow.service.LoanCalculatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/simulator")
@RequiredArgsConstructor
public class SimulatorController {
    private final LoanCalculatorService calculatorService;
    @PostMapping
    public ResponseEntity<SimulatorResponse> simulate(@RequestBody SimulatorRequest request) {

        // El usuario ingresa 2% mensual → el sistema lo convierte a 24% anual internamente
        BigDecimal annualRate = request.getMonthlyInterestRate()
                .multiply(BigDecimal.valueOf(12));

        SimulatorResponse response = calculatorService.generateAmortizationTable(
                request.getAmount(),
                annualRate,
                request.getTermMonths()
        );
        return ResponseEntity.ok(response);
    }
}
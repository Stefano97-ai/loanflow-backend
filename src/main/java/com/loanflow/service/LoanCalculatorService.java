package com.loanflow.service;

import com.loanflow.dto.response.SimulatorInstallmentResponse;
import com.loanflow.dto.response.SimulatorResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanCalculatorService {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    public BigDecimal calculateMonthlyPayment(BigDecimal principal, BigDecimal annualRate, int termMonths) {
        BigDecimal monthlyRate = annualRate
                .divide(BigDecimal.valueOf(12), 10, ROUNDING)
                .divide(BigDecimal.valueOf(100), 10, ROUNDING);

        // Interés simple: cuota = (principal / n) + (principal × tasa mensual)
        BigDecimal monthlyPrincipal = principal.divide(BigDecimal.valueOf(termMonths), SCALE, ROUNDING);
        BigDecimal monthlyInterest = principal.multiply(monthlyRate).setScale(SCALE, ROUNDING);

        return monthlyPrincipal.add(monthlyInterest);
    }

    public SimulatorResponse generateAmortizationTable(BigDecimal principal, BigDecimal annualRate, int termMonths) {
        BigDecimal monthlyRate = annualRate
                .divide(BigDecimal.valueOf(12), 10, ROUNDING)
                .divide(BigDecimal.valueOf(100), 10, ROUNDING);

        // Interés fijo sobre capital original (no sobre saldo)
        BigDecimal monthlyInterest = principal.multiply(monthlyRate).setScale(SCALE, ROUNDING);
        BigDecimal monthlyPrincipal = principal.divide(BigDecimal.valueOf(termMonths), SCALE, ROUNDING);
        BigDecimal monthlyPayment = monthlyPrincipal.add(monthlyInterest);

        BigDecimal remainingBalance = principal;
        List<SimulatorInstallmentResponse> installments = new ArrayList<>();
        LocalDate startDate = LocalDate.now();

        for (int i = 1; i <= termMonths; i++) {
            // Última cuota ajusta el saldo restante por redondeo
            BigDecimal principal_i = (i == termMonths) ? remainingBalance : monthlyPrincipal;
            BigDecimal total_i = principal_i.add(monthlyInterest);
            remainingBalance = remainingBalance.subtract(principal_i).setScale(SCALE, ROUNDING);
            if (remainingBalance.compareTo(BigDecimal.ZERO) < 0) remainingBalance = BigDecimal.ZERO;

            installments.add(SimulatorInstallmentResponse.builder()
                    .installmentNumber(i)
                    .dueDate(startDate.plusMonths(i))
                    .principalAmount(principal_i)
                    .interestAmount(monthlyInterest)
                    .totalAmount(total_i)
                    .remainingBalance(remainingBalance)
                    .build());
        }

        BigDecimal totalAmount = installments.stream()
                .map(SimulatorInstallmentResponse::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(SCALE, ROUNDING);

        BigDecimal totalInterest = totalAmount.subtract(principal).setScale(SCALE, ROUNDING);

        return SimulatorResponse.builder()
                .amount(principal)
                .monthlyInterestRate(annualRate.divide(BigDecimal.valueOf(12), SCALE, ROUNDING))
                .termMonths(termMonths)
                .monthlyPayment(monthlyPayment)
                .totalAmount(totalAmount)
                .totalInterest(totalInterest)
                .installments(installments)
                .build();
    }
}

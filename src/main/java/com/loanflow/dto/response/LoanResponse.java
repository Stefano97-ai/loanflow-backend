package com.loanflow.dto.response;

import com.loanflow.enums.LoanStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LoanResponse {
    private Long id;
    private String userName;
    private String userEmail;
    private String loanTypeName;
    private BigDecimal amount;
    private BigDecimal annualInterestRate;
    private Integer termMonths;
    private BigDecimal monthlyPayment;
    private BigDecimal totalAmount;
    private String purpose;
    private LoanStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private List<InstallmentResponse> installments;

    // ✅ NUEVOS — para mostrar progreso de cuotas sin cargar la lista completa
    private Integer installmentCount;
    private Integer paidInstallments;
}
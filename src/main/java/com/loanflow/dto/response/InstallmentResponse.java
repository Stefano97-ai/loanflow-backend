package com.loanflow.dto.response;
import com.loanflow.enums.InstallmentStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class InstallmentResponse {
    private Long id;
    private Integer installmentNumber;
    private LocalDate dueDate;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private BigDecimal totalAmount;
    private BigDecimal remainingBalance;
    private InstallmentStatus status;
}

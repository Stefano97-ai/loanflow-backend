package com.loanflow.dto.response;
import lombok.*;
import java.math.BigDecimal;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LoanTypeResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private Integer minMonths;
    private Integer maxMonths;
    private BigDecimal annualInterestRate;
}

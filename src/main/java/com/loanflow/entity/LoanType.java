package com.loanflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "loan_types")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LoanType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(name = "min_amount", nullable = false)
    private BigDecimal minAmount;

    @Column(name = "max_amount", nullable = false)
    private BigDecimal maxAmount;

    @Column(name = "min_months", nullable = false)
    private Integer minMonths;

    @Column(name = "max_months", nullable = false)
    private Integer maxMonths;

    // Tasa de interés anual (%)
    @Column(name = "annual_interest_rate", nullable = false)
    private BigDecimal annualInterestRate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "loanType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Loan> loans;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}

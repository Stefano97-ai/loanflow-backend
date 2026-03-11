package com.loanflow.entity;

import com.loanflow.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "loans")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_type_id", nullable = false)
    private LoanType loanType;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "annual_interest_rate", nullable = false)
    private BigDecimal annualInterestRate;

    @Column(name = "term_months", nullable = false)
    private Integer termMonths;

    @Column(name = "monthly_payment", nullable = false)
    private BigDecimal monthlyPayment;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    private String purpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LoanStatus status = LoanStatus.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Installment> installments;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}

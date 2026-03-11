package com.loanflow.entity;

import com.loanflow.enums.InstallmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "installments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Installment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(name = "installment_number", nullable = false)
    private Integer installmentNumber;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "principal_amount", nullable = false)
    private BigDecimal principalAmount;

    @Column(name = "interest_amount", nullable = false)
    private BigDecimal interestAmount;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "remaining_balance", nullable = false)
    private BigDecimal remainingBalance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InstallmentStatus status = InstallmentStatus.PENDING;

    @OneToMany(mappedBy = "installment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments;
}

package com.loanflow.repository;

import com.loanflow.entity.Loan;
import com.loanflow.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    // Préstamos de un usuario específico
    List<Loan> findByUserId(Long userId);

    // Préstamos por estado
    List<Loan> findByStatus(LoanStatus status);

    // Contar préstamos por estado (para stats del admin)
    long countByStatus(LoanStatus status);

    // Suma total de montos aprobados
    @Query("SELECT COALESCE(SUM(l.amount), 0) FROM Loan l WHERE l.status = 'APPROVED' OR l.status = 'COMPLETED'")
    BigDecimal sumApprovedAmounts();
}

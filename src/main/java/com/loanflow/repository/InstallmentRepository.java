package com.loanflow.repository;

import com.loanflow.entity.Installment;
import com.loanflow.enums.InstallmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InstallmentRepository extends JpaRepository<Installment, Long> {


    List<Installment> findByLoanIdOrderByInstallmentNumber(Long loanId);

    @Query("SELECT i FROM Installment i WHERE i.status = :status AND i.dueDate < :today")
    List<Installment> findOverdueInstallments(
            @Param("today") LocalDate today,
            @Param("status") InstallmentStatus status
    );

    long countByLoanIdAndStatus(Long loanId,InstallmentStatus status);

    Optional<Installment> findByLoanIdAndInstallmentNumber(Long loanId, Integer installmentNumber);




}

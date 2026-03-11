package com.loanflow.repository;

import com.loanflow.entity.LoanType;
import com.loanflow.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByInstallmentLoanIdOrderByPaymentDateDesc(Long loanId);
    List<Payment> findAllByOrderByPaymentDateDesc();

}

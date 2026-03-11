package com.loanflow.service;

import com.loanflow.dto.response.DashboardStatsResponse;
import com.loanflow.enums.InstallmentStatus;
import com.loanflow.enums.LoanStatus;
import com.loanflow.repository.InstallmentRepository;
import com.loanflow.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final LoanRepository loanRepository;
    private final InstallmentRepository installmentRepository;

    public DashboardStatsResponse getStats() {
        long overdueCount = installmentRepository
                .findOverdueInstallments(LocalDate.now(), InstallmentStatus.PENDING).size();

        return DashboardStatsResponse.builder()
                .totalLoans(loanRepository.count())
                .pendingLoans(loanRepository.countByStatus(LoanStatus.PENDING))
                .approvedLoans(loanRepository.countByStatus(LoanStatus.APPROVED))
                .rejectedLoans(loanRepository.countByStatus(LoanStatus.REJECTED))
                .completedLoans(loanRepository.countByStatus(LoanStatus.COMPLETED))
                .totalAmountDisbursed(loanRepository.sumApprovedAmounts())
                .overdueInstallments(overdueCount)
                .build();
    }
}

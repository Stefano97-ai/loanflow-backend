package com.loanflow.dto.response;
import lombok.*;
import java.math.BigDecimal;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardStatsResponse {
    private long totalLoans;
    private long pendingLoans;
    private long approvedLoans;
    private long rejectedLoans;
    private long completedLoans;
    private BigDecimal totalAmountDisbursed;
    private long overdueInstallments;
}

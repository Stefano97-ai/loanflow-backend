package com.loanflow.dto.request;
import com.loanflow.enums.LoanStatus;
import lombok.Data;
@Data
public class LoanStatusRequest {
    private LoanStatus status;
    private String rejectionReason;
}

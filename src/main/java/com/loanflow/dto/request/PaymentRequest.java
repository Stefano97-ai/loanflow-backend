package com.loanflow.dto.request;
import com.loanflow.enums.PaymentMethod;
import lombok.Data;
@Data
public class PaymentRequest {
    private Long loanId;              // ¿de qué préstamo?
    private Integer installmentNumber; // ¿qué número de cuota?
    private PaymentMethod paymentMethod;
    private String notes;
}

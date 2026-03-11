package com.loanflow.dto.response;

import com.loanflow.enums.PaymentMethod;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long installmentId;
    private Integer installmentNumber; // qué cuota se pagó (ej: cuota 1 de 12)
    private BigDecimal amountPaid;
    private LocalDateTime paymentDate;
    private PaymentMethod paymentMethod;
    private String notes;
    private Long loanId;
    private String clientName;
    private String clientEmail;
}
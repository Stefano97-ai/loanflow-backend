package com.loanflow.service;

import com.loanflow.dto.request.PaymentRequest;
import com.loanflow.dto.response.PaymentResponse;
import com.loanflow.entity.Installment;
import com.loanflow.entity.Payment;
import com.loanflow.enums.InstallmentStatus;
import com.loanflow.enums.LoanStatus;
import com.loanflow.repository.InstallmentRepository;
import com.loanflow.repository.LoanRepository;
import com.loanflow.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final InstallmentRepository installmentRepository;
    private final LoanRepository loanRepository;

    @Transactional
    // DESPUÉS
    public PaymentResponse payInstallment(PaymentRequest request, String userEmail, boolean esAdmin) {
        // Buscamos la cuota por préstamo + número de cuota
        Installment installment = installmentRepository
                .findByLoanIdAndInstallmentNumber(request.getLoanId(), request.getInstallmentNumber())
                .orElseThrow(() -> new RuntimeException("Cuota no encontrada"));

        // DESPUÉS
        if (!esAdmin) {
            String propietario = installment.getLoan().getUser().getEmail();
            if (!propietario.equals(userEmail)) {
                throw new RuntimeException("No tienes permiso para pagar esta cuota");
            }
        }

        if (installment.getStatus() == InstallmentStatus.PAID) {
            throw new RuntimeException("Esta cuota ya fue pagada");
        }

        Payment payment = Payment.builder()
                .installment(installment)
                .amountPaid(installment.getTotalAmount())
                .paymentMethod(request.getPaymentMethod())
                .notes(request.getNotes())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        installment.setStatus(InstallmentStatus.PAID);
        installmentRepository.save(installment);

        checkAndCompleteLoan(installment.getLoan().getId());

        // Devolvemos DTO en lugar de la entidad — sin recursión infinita
        return toResponse(savedPayment);
    }

    public List<PaymentResponse> getPaymentsByLoan(Long loanId) {
        return paymentRepository
                .findByInstallmentLoanIdOrderByPaymentDateDesc(loanId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void checkAndCompleteLoan(Long loanId) {
        long pendingCount = installmentRepository.countByLoanIdAndStatus(loanId, InstallmentStatus.PENDING);
        if (pendingCount == 0) {
            loanRepository.findById(loanId).ifPresent(loan -> {
                loan.setStatus(LoanStatus.COMPLETED);
                loanRepository.save(loan);
            });
        }
    }

    // Convierte la entidad Payment al DTO PaymentResponse — plano, sin relaciones JPA
    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .installmentId(payment.getInstallment().getId())
                .installmentNumber(payment.getInstallment().getInstallmentNumber())
                .amountPaid(payment.getAmountPaid())
                .paymentDate(payment.getPaymentDate())
                .paymentMethod(payment.getPaymentMethod())
                .notes(payment.getNotes())
                .loanId(payment.getInstallment().getLoan().getId())
                .clientName(payment.getInstallment().getLoan().getUser().getName())
                .clientEmail(payment.getInstallment().getLoan().getUser().getEmail())
                .build();
    }


    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAllByOrderByPaymentDateDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
package com.loanflow.controller;

import com.loanflow.dto.request.PaymentRequest;
import com.loanflow.dto.response.PaymentResponse;
import com.loanflow.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> pay(
            @RequestBody PaymentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean esAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(
                paymentService.payInstallment(request, userDetails.getUsername(), esAdmin)
        );
    }

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<PaymentResponse>> getByLoan(@PathVariable Long loanId) {
        return ResponseEntity.ok(paymentService.getPaymentsByLoan(loanId));
    }

    // NUEVO — Historial general de todos los pagos (ADMIN)
    @GetMapping("/all")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
}
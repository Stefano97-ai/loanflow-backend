package com.loanflow.controller;

import com.loanflow.dto.request.LoanRequest;
import com.loanflow.dto.request.LoanStatusRequest;
import com.loanflow.dto.response.LoanResponse;
import com.loanflow.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @GetMapping("/{id}")
    public ResponseEntity<LoanResponse> getLoanById(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(
            @Valid @RequestBody LoanRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        boolean esAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!esAdmin && request.getTasaInteresPersonalizada() != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(
                loanService.createLoan(request, userDetails.getUsername(), esAdmin)
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<LoanResponse>> getMyLoans(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(loanService.getMyLoans(userDetails.getUsername()));
    }

    // ← getLoanDetail ELIMINADO (era duplicado de /{id})

    @GetMapping("/all")
    public ResponseEntity<List<LoanResponse>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<LoanResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody LoanStatusRequest request) {
        return ResponseEntity.ok(loanService.updateLoanStatus(id, request));
    }
}
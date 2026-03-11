package com.loanflow.controller;

import com.loanflow.dto.request.CambioDeContraseñaRequest;
import com.loanflow.dto.request.LoginRequest;
import com.loanflow.dto.request.RegisterRequest;
import com.loanflow.dto.response.AuthResponse;
import com.loanflow.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        boolean esAdmin = userDetails != null && userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return ResponseEntity.ok(authService.register(request, esAdmin)); // ← agrega esAdmin
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody CambioDeContraseñaRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        authService.changePassword(request, userDetails.getUsername());
        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }
}

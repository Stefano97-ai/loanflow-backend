package com.loanflow.service;

import com.loanflow.dto.request.CambioDeContraseñaRequest;
import com.loanflow.dto.request.LoginRequest;
import com.loanflow.dto.request.RegisterRequest;
import com.loanflow.dto.response.AuthResponse;
import com.loanflow.entity.User;
import com.loanflow.enums.Role;
import com.loanflow.repository.UserRepository;
import com.loanflow.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse login(LoginRequest request) {
        // Buscamos el usuario directamente - sin authenticationManager
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + request.getEmail()));



        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Password incorrecto");
        }


        String token = jwtTokenProvider.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public AuthResponse register(RegisterRequest request, boolean esAdmin) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (userRepository.existsByDni(request.getDni())) {
            throw new RuntimeException("El DNI ya está registrado");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .dni(request.getDni())
                .phone(request.getPhone())
                .role(Role.CLIENT)
                .build();

        userRepository.save(user);

        // Si es admin → sin token, solo mensaje
        if (esAdmin) {
            return AuthResponse.builder()
                    .userId(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole()) // ← falta esto
                    .message("Usuario registrado exitosamente")
                    .build();
        }

        // Si es cliente → con token
        String token = jwtTokenProvider.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public void changePassword(CambioDeContraseñaRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Contraseña actual incorrecta");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
package com.loanflow.controller;

import com.loanflow.entity.User;
import com.loanflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // Lista todos los clientes (ADMIN)
    @GetMapping("/all")
    public ResponseEntity<?> getAllClients() {
        List<User> users = userRepository.findAllClients();
        return ResponseEntity.ok(users.stream().map(this::toMap).toList());
    }

    // Buscar clientes (ADMIN)
    @GetMapping("/search")
    public ResponseEntity<?> searchClients(@RequestParam String q) {
        List<User> users = userRepository.searchClients(q);
        return ResponseEntity.ok(users.stream().map(u -> Map.of(
                "id", u.getId(),
                "name", u.getName(),
                "email", u.getEmail(),
                "dni", u.getDni()
        )).toList());
    }

    // Ver mi perfil (cualquier usuario autenticado)
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(toMap(user));
    }

    // Actualizar mi perfil (cualquier usuario autenticado)
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> body) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (body.containsKey("name") && !body.get("name").isBlank()) {
            user.setName(body.get("name"));
        }
        if (body.containsKey("phone")) {
            user.setPhone(body.get("phone"));
        }
        // Email y DNI no se pueden cambiar por seguridad

        userRepository.save(user);
        return ResponseEntity.ok(toMap(user));
    }

    private Map<String, Object> toMap(User u) {
        return Map.of(
                "id", u.getId(),
                "name", u.getName(),
                "email", u.getEmail(),
                "dni", u.getDni(),
                "phone", u.getPhone() != null ? u.getPhone() : "",
                "role", u.getRole().name(),
                "createdAt", u.getCreatedAt() != null ? u.getCreatedAt().toString() : ""
        );
    }
}
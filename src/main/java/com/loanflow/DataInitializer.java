package com.loanflow;

import com.loanflow.entity.LoanType;
import com.loanflow.entity.User;
import com.loanflow.enums.Role;
import com.loanflow.repository.LoanTypeRepository;
import com.loanflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {

        if (loanTypeRepository.count() == 0) {
            loanTypeRepository.save(LoanType.builder()
                    .name("Préstamo Personal")
                    .description("Para gastos personales, emergencias o imprevistos")
                    .minAmount(new BigDecimal("200.00"))
                    .maxAmount(new BigDecimal("5000.00"))
                    .minMonths(1).maxMonths(12)
                    .annualInterestRate(new BigDecimal("36.00"))
                    .build());

            loanTypeRepository.save(LoanType.builder()
                    .name("Préstamo para Negocio")
                    .description("Capital de trabajo, compra de mercadería o equipos")
                    .minAmount(new BigDecimal("500.00"))
                    .maxAmount(new BigDecimal("15000.00"))
                    .minMonths(3).maxMonths(18)
                    .annualInterestRate(new BigDecimal("30.00"))
                    .build());

            loanTypeRepository.save(LoanType.builder()
                    .name("Préstamo Vehicular")
                    .description("Para mototaxis, motos o vehículos de trabajo")
                    .minAmount(new BigDecimal("2000.00"))
                    .maxAmount(new BigDecimal("25000.00"))
                    .minMonths(6).maxMonths(24)
                    .annualInterestRate(new BigDecimal("24.00"))
                    .build());

            loanTypeRepository.save(LoanType.builder()
                    .name("Préstamo Educativo")
                    .description("Para estudios técnicos, universitarios o cursos")
                    .minAmount(new BigDecimal("200.00"))
                    .maxAmount(new BigDecimal("5000.00"))
                    .minMonths(3).maxMonths(12)
                    .annualInterestRate(new BigDecimal("18.00"))
                    .build());

            loanTypeRepository.save(LoanType.builder()
                    .name("Préstamo Personalizado")
                    .description("Condiciones negociadas directamente con el administrador")
                    .minAmount(new BigDecimal("100.00"))
                    .maxAmount(new BigDecimal("30000.00"))
                    .minMonths(1).maxMonths(36)
                    .annualInterestRate(new BigDecimal("0.00"))
                    .build());

            System.out.println("Tipos de prestamo creados");
        }

        // ===== USUARIOS =====
        if (!userRepository.existsByEmail("admin@loanflow.com")) {
            userRepository.save(User.builder()
                    .name("Admin LoanFlow")
                    .email("admin@loanflow.com")
                    .password(passwordEncoder.encode("admin123"))
                    .dni("00000001")
                    .phone("999000001")
                    .role(Role.ADMIN)
                    .build());
        }

        if (!userRepository.existsByEmail("carlos@gmail.com")) {
            userRepository.save(User.builder()
                    .name("Carlos Ramirez")
                    .email("carlos@gmail.com")
                    .password(passwordEncoder.encode("admin123"))
                    .dni("12345678")
                    .phone("987654321")
                    .role(Role.CLIENT)
                    .build());
        }

        if (!userRepository.existsByEmail("maria@gmail.com")) {
            userRepository.save(User.builder()
                    .name("Maria Torres")
                    .email("maria@gmail.com")
                    .password(passwordEncoder.encode("admin123"))
                    .dni("87654321")
                    .phone("976543210")
                    .role(Role.CLIENT)
                    .build());
        }

        System.out.println("✅ Datos iniciales verificados");
    }
}
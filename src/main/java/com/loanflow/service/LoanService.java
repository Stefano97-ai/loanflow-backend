package com.loanflow.service;

import com.loanflow.dto.request.LoanRequest;
import com.loanflow.dto.request.LoanStatusRequest;
import com.loanflow.dto.response.InstallmentResponse;
import com.loanflow.dto.response.LoanResponse;
import com.loanflow.dto.response.SimulatorResponse;
import com.loanflow.entity.Installment;
import com.loanflow.entity.Loan;
import com.loanflow.entity.LoanType;
import com.loanflow.entity.User;
import com.loanflow.enums.InstallmentStatus;
import com.loanflow.enums.LoanStatus;
import com.loanflow.repository.InstallmentRepository;
import com.loanflow.repository.LoanRepository;
import com.loanflow.repository.LoanTypeRepository;
import com.loanflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final UserRepository userRepository;
    private final InstallmentRepository installmentRepository;
    private final LoanCalculatorService calculatorService;

    @Transactional
    public LoanResponse createLoan(LoanRequest request, String userEmail, boolean esAdmin) {

        // ✅ CORREGIDO: lógica correcta para determinar a quién pertenece el préstamo
        User user;
        if (esAdmin && request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            if (user.getEmail().equals(userEmail)) {
                throw new RuntimeException("El administrador no puede crearse préstamos a sí mismo");
            }
        } else {
            user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        }

        // ✅ CORRECTO — esto ya lo tenías bien, no cambies nada de aquí hacia abajo
        LoanType loanType;
        if (request.getLoanTypeId() != null) {
            loanType = loanTypeRepository.findById(request.getLoanTypeId())
                    .orElseThrow(() -> new RuntimeException("Tipo de préstamo no encontrado"));
        } else if (esAdmin) {
            loanType = loanTypeRepository.findByName("Préstamo Personalizado")
                    .orElseThrow(() -> new RuntimeException("Tipo personalizado no configurado"));
        } else {
            throw new RuntimeException("Debes seleccionar un tipo de préstamo");
        }

        BigDecimal tasaAnual = (esAdmin && request.getTasaInteresPersonalizada() != null)
                ? request.getTasaInteresPersonalizada().multiply(BigDecimal.valueOf(12))
                : loanType.getAnnualInterestRate();

        BigDecimal cuotaMensual = calculatorService.calculateMonthlyPayment(
                request.getAmount(), tasaAnual, request.getTermMonths());
        BigDecimal totalPagar = cuotaMensual.multiply(BigDecimal.valueOf(request.getTermMonths()));

        LoanStatus estado = esAdmin ? LoanStatus.APPROVED : LoanStatus.PENDING;
        LocalDateTime fechaAprobacion = esAdmin ? LocalDateTime.now() : null;

        Loan loan = Loan.builder()
                .user(user).loanType(loanType).amount(request.getAmount())
                .annualInterestRate(tasaAnual)
                .termMonths(request.getTermMonths())
                .monthlyPayment(cuotaMensual)
                .totalAmount(totalPagar)
                .purpose(request.getPurpose())
                .status(estado)
                .approvedAt(fechaAprobacion)
                .build();

        Loan prestamoGuardado = loanRepository.save(loan);

        if (esAdmin) {
            generateInstallments(prestamoGuardado);
        }

        return toResponse(prestamoGuardado, esAdmin);
    }


    public List<LoanResponse> getMyLoans(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return loanRepository.findByUserId(user.getId()).stream()
                .map(loan -> toResponse(loan, true)) // ← cambiar false por true
                .toList();
    }

    public LoanResponse getLoanDetail(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prestamo no encontrado"));
        return toResponse(loan, true);
    }

    public List<LoanResponse> getAllLoans() {
        return loanRepository.findAll().stream().map(loan -> toResponse(loan, false)).toList();
    }

    @Transactional
    public LoanResponse updateLoanStatus(Long id, LoanStatusRequest request) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prestamo no encontrado"));
        loan.setStatus(request.getStatus());
        if (request.getStatus() == LoanStatus.APPROVED) {
            loan.setApprovedAt(LocalDateTime.now());
            loanRepository.save(loan);
            generateInstallments(loan);
        } else {
            loanRepository.save(loan);
        }
        return toResponse(loan, true);
    }

    private void generateInstallments(Loan loan) {
        SimulatorResponse simulation = calculatorService.generateAmortizationTable(
                loan.getAmount(), loan.getAnnualInterestRate(), loan.getTermMonths());
        simulation.getInstallments().forEach(sim -> {
            Installment inst = Installment.builder()
                    .loan(loan).installmentNumber(sim.getInstallmentNumber())
                    .dueDate(sim.getDueDate()).principalAmount(sim.getPrincipalAmount())
                    .interestAmount(sim.getInterestAmount()).totalAmount(sim.getTotalAmount())
                    .remainingBalance(sim.getRemainingBalance()).status(InstallmentStatus.PENDING).build();
            installmentRepository.save(inst);
        });
    }

    private LoanResponse toResponse(Loan loan, boolean includeInstallments) {
        // ✅ Siempre consultar contadores de cuotas
        List<Installment> allInstallments = installmentRepository
                .findByLoanIdOrderByInstallmentNumber(loan.getId());

        long paid = allInstallments.stream()
                .filter(i -> i.getStatus() == InstallmentStatus.PAID)
                .count();

        LoanResponse.LoanResponseBuilder builder = LoanResponse.builder()
                .id(loan.getId())
                .userName(loan.getUser().getName())
                .userEmail(loan.getUser().getEmail())
                .loanTypeName(loan.getLoanType().getName())
                .amount(loan.getAmount())
                .annualInterestRate(loan.getAnnualInterestRate())
                .termMonths(loan.getTermMonths())
                .monthlyPayment(loan.getMonthlyPayment())
                .totalAmount(loan.getTotalAmount())
                .purpose(loan.getPurpose())
                .status(loan.getStatus())
                .createdAt(loan.getCreatedAt())
                .approvedAt(loan.getApprovedAt())
                .installmentCount(allInstallments.size())
                .paidInstallments((int) paid);

        // Solo incluir la lista detallada si se solicita
        if (includeInstallments) {
            List<InstallmentResponse> cuotas = allInstallments.stream()
                    .map(inst -> InstallmentResponse.builder()
                            .id(inst.getId())
                            .installmentNumber(inst.getInstallmentNumber())
                            .dueDate(inst.getDueDate())
                            .principalAmount(inst.getPrincipalAmount())
                            .interestAmount(inst.getInterestAmount())
                            .totalAmount(inst.getTotalAmount())
                            .remainingBalance(inst.getRemainingBalance())
                            .status(inst.getStatus())
                            .build())
                    .toList();
            builder.installments(cuotas);
        }

        return builder.build();
    }

    public LoanResponse getLoanById(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
        return toResponse(loan, true);
    }
}

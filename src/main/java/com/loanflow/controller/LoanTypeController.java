package com.loanflow.controller;

import com.loanflow.dto.response.LoanTypeResponse;
import com.loanflow.entity.LoanType;
import com.loanflow.repository.LoanTypeRepository;
import com.loanflow.service.LoanTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loan-types")
@RequiredArgsConstructor
public class LoanTypeController {

    private final LoanTypeService loanTypeService;
    private final LoanTypeRepository loanTypeRepository;

    @GetMapping
    public ResponseEntity<List<LoanTypeResponse>> getAll() {
        return ResponseEntity.ok(loanTypeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanTypeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(loanTypeService.findById(id));
    }

    // CRUD para ADMIN
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        LoanType lt = LoanType.builder()
                .name((String) body.get("name"))
                .description((String) body.get("description"))
                .minAmount(new BigDecimal(body.get("minAmount").toString()))
                .maxAmount(new BigDecimal(body.get("maxAmount").toString()))
                .minMonths((Integer) body.get("minMonths"))
                .maxMonths((Integer) body.get("maxMonths"))
                .annualInterestRate(new BigDecimal(body.get("annualInterestRate").toString()))
                .build();
        loanTypeRepository.save(lt);
        return ResponseEntity.ok(loanTypeService.findById(lt.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        LoanType lt = loanTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo no encontrado"));
        if (body.containsKey("name")) lt.setName((String) body.get("name"));
        if (body.containsKey("description")) lt.setDescription((String) body.get("description"));
        if (body.containsKey("minAmount")) lt.setMinAmount(new BigDecimal(body.get("minAmount").toString()));
        if (body.containsKey("maxAmount")) lt.setMaxAmount(new BigDecimal(body.get("maxAmount").toString()));
        if (body.containsKey("minMonths")) lt.setMinMonths((Integer) body.get("minMonths"));
        if (body.containsKey("maxMonths")) lt.setMaxMonths((Integer) body.get("maxMonths"));
        if (body.containsKey("annualInterestRate")) lt.setAnnualInterestRate(new BigDecimal(body.get("annualInterestRate").toString()));
        loanTypeRepository.save(lt);
        return ResponseEntity.ok(loanTypeService.findById(lt.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        loanTypeRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Tipo de prestamo eliminado"));
    }
}
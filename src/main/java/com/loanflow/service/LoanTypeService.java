package com.loanflow.service;
import com.loanflow.dto.response.LoanTypeResponse;
import com.loanflow.entity.LoanType;
import com.loanflow.repository.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class LoanTypeService {
    private final LoanTypeRepository loanTypeRepository;
    public List<LoanTypeResponse> findAll() {
        return loanTypeRepository.findAll().stream().map(this::toResponse).toList();
    }
    public LoanTypeResponse findById(Long id) {
        LoanType lt = loanTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo no encontrado"));
        return toResponse(lt);
    }
    private LoanTypeResponse toResponse(LoanType lt) {
        return LoanTypeResponse.builder()
                .id(lt.getId())
                .name(lt.getName())
                .description(lt.getDescription())
                .minAmount(lt.getMinAmount())
                .maxAmount(lt.getMaxAmount())
                .minMonths(lt.getMinMonths())
                .maxMonths(lt.getMaxMonths())
                .annualInterestRate(lt.getAnnualInterestRate()) // ✅ NUEVO
                .build();
    }
}

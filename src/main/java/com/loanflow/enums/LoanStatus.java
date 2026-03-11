package com.loanflow.enums;

public enum LoanStatus {
    PENDING,    // Esperando aprobación del admin
    APPROVED,   // Aprobado - cuotas generadas
    REJECTED,   // Rechazado por admin
    COMPLETED   // Todas las cuotas pagadas
}

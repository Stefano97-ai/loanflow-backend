package com.loanflow.dto.request;

import lombok.Data;

@Data
public class CambioDeContraseñaRequest {
    private String currentPassword;
    private String newPassword;
}

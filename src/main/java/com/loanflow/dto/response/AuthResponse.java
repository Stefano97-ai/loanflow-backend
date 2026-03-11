package com.loanflow.dto.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.loanflow.enums.Role;
import lombok.*;
@Data
@Builder
public class AuthResponse {
    private String token;
    private Long userId;
    private String name;
    private String email;
    private Role role;

    @JsonInclude(JsonInclude.Include.NON_NULL) // ← solo en este campo
    private String message;
}

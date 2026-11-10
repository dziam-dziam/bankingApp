package innowise.bankingApp.auth.model.dto.response;

import innowise.bankingApp.auth.model.Role;

import java.time.Instant;

public record ResponseTokenValidationDto(
        boolean valid,
        Long userId,
        Role role,
        Instant expiresAt
) {
}
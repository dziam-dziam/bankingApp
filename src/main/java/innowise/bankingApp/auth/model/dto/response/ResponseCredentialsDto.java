package innowise.bankingApp.auth.model.dto.response;

import innowise.bankingApp.auth.model.Role;

public record ResponseCredentialsDto(
        Long id,
        Long userId,
        String login,
        Role role
) {
}
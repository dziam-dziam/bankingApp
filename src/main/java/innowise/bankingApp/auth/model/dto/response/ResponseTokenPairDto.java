package innowise.bankingApp.auth.model.dto.response;

public record ResponseTokenPairDto(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {
}
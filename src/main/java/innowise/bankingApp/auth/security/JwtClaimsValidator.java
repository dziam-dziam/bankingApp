package innowise.bankingApp.auth.security;

import innowise.bankingApp.auth.model.Role;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;

public class JwtClaimsValidator implements OAuth2TokenValidator<Jwt> {

    private static final OAuth2Error INVALID_TOKEN =
            new OAuth2Error("invalid_token", "Invalid access token", null);

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        Object rawId = claims.get("user_id");

        long userId;
        try {
            userId = Long.parseLong(String.valueOf(rawId));
        } catch (NumberFormatException exception) {
            return OAuth2TokenValidatorResult.failure(INVALID_TOKEN);
        }

        boolean valid = userId > 0
                && Long.toString(userId).equals(claims.get("sub"))
                && claims.get("aud") instanceof Collection<?> audiences
                && audiences.contains("bankingApp")
                && claims.get("jti") instanceof String tokenId
                && !tokenId.isBlank()
                && claims.get("iat") instanceof Instant issuedAt
                && claims.get("nbf") instanceof Instant notBefore
                && claims.get("exp") instanceof Instant expiresAt
                && issuedAt.isBefore(expiresAt)
                && notBefore.isBefore(expiresAt)
                && (Role.ADMIN.name().equals(claims.get("role"))
                || Role.USER.name().equals(claims.get("role")))
                && ("access".equals(claims.get("token_use"))
                || "refresh".equals(claims.get("token_use")));

        return valid
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(INVALID_TOKEN);
    }
}
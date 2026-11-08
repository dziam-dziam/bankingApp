package innowise.bankingApp.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("auth.jwt")
public record JwtProperties(
        String secret,
        String issuer,
        Duration accessTtl,
        Duration refreshTtl
) {
    public JwtProperties {
        if (secret == null || issuer == null || issuer.isBlank()
                || accessTtl == null || refreshTtl == null
                || accessTtl.toSeconds() < 1
                || refreshTtl.compareTo(accessTtl) <= 0) {
            throw new IllegalArgumentException(
                    "Configure JWT secret, issuer and valid access/refresh lifetimes"
            );
        }
    }
}
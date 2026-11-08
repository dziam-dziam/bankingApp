package innowise.bankingApp.auth.service;

import innowise.bankingApp.auth.config.JwtProperties;
import innowise.bankingApp.auth.model.dto.response.ResponseTokenPairDto;
import innowise.bankingApp.auth.model.entity.CredentialsEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final JwtEncoder encoder;
    private final JwtProperties properties;
    private final Clock clock;

    public ResponseTokenPairDto generateTokens(CredentialsEntity credentials) {
        return new ResponseTokenPairDto(
                encode(credentials, "access", properties.accessTtl()),
                encode(credentials, "refresh", properties.refreshTtl()),
                "Bearer",
                properties.accessTtl().toSeconds()
        );
    }

    public String hashRefreshToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    private String encode(
            CredentialsEntity credentials,
            String purpose,
            Duration lifetime
    ) {
        Instant now = clock.instant();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(properties.issuer())
                .subject(credentials.getUserId().toString())
                .audience(List.of("bankingApp"))
                .issuedAt(now)
                .notBefore(now)
                .expiresAt(now.plus(lifetime))
                .id(UUID.randomUUID().toString())
                .claim("user_id", credentials.getUserId())
                .claim("role", credentials.getRole().name())
                .claim("token_use", purpose)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return encoder.encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }
}
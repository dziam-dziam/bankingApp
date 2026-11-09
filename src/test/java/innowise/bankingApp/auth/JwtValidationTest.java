package innowise.bankingApp.auth;

import innowise.bankingApp.auth.config.JwtConfig;
import innowise.bankingApp.auth.config.JwtProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwsHeader;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtValidationTest {

    private static final Instant NOW = Instant.parse("2026-01-01T12:00:00Z");
    private static final JwtProperties PROPERTIES = new JwtProperties(
            Base64.getEncoder().encodeToString(new byte[32]),
            "bankingApp", Duration.ofMinutes(15), Duration.ofDays(7));
    private static final JwsHeader HEADER = JwsHeader.with(MacAlgorithm.HS256).build();

    private final JwtConfig config = new JwtConfig();
    private final SecretKey key = config.jwtSecretKey(PROPERTIES);
    private final JwtEncoder encoder = config.jwtEncoder(key);
    private final JwtDecoder decoder = config.jwtDecoder(key, PROPERTIES, Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void shouldAcceptSignedTokenWithValidClaims() {
        Jwt decoded = decoder.decode(sign(validClaims()));

        assertThat(decoded.getSubject()).isEqualTo("42");
        assertThat(decoded.getAudience()).containsExactly("bankingApp");
        assertThat(decoded.getClaimAsString("token_use")).isEqualTo("access");
        assertThat(decoded.getExpiresAt()).isEqualTo(NOW.plusSeconds(60));
    }

    @ParameterizedTest(name = "{0} = {1}")
    @MethodSource("invalidClaims")
    void shouldRejectCorrectlySignedTokensWithInvalidClaims(String claim, Object value) {
        JwtClaimsSet.Builder claims = validClaims().claims(values -> {
            if (value == null) {
                values.remove(claim);
            } else {
                values.put(claim, value);
            }
        });
        String token = sign(claims);

        assertThatThrownBy(() -> decoder.decode(token)).isInstanceOf(JwtException.class);
    }

    static Stream<Arguments> invalidClaims() {
        return Stream.of(
                Arguments.of("user_id", "not-an-id"),
                Arguments.of("user_id", 0L),
                Arguments.of("sub", "43"),
                Arguments.of("aud", null),
                Arguments.of("aud", List.of("another-service")),
                Arguments.of("jti", null),
                Arguments.of("jti", " "),
                Arguments.of("iat", null),
                Arguments.of("nbf", null),
                Arguments.of("nbf", NOW.plusSeconds(60)),
                Arguments.of("exp", null),
                Arguments.of("exp", NOW.minusSeconds(1)),
                Arguments.of("role", "GUEST"),
                Arguments.of("token_use", "unknown"),
                Arguments.of("iss", "another-issuer"));
    }

    @Test
    void shouldRejectTokenSignedByAnotherKey() {
        byte[] otherKey = new byte[32];
        otherKey[0] = 1;
        JwtProperties otherProperties = new JwtProperties(
                Base64.getEncoder().encodeToString(otherKey), PROPERTIES.issuer(),
                PROPERTIES.accessTtl(), PROPERTIES.refreshTtl());
        JwtEncoder otherEncoder = config.jwtEncoder(config.jwtSecretKey(otherProperties));
        String token = otherEncoder.encode(JwtEncoderParameters.from(HEADER, validClaims().build())).getTokenValue();

        assertThatThrownBy(() -> decoder.decode(token)).isInstanceOf(JwtException.class);
    }

    private static JwtClaimsSet.Builder validClaims() {
        return JwtClaimsSet.builder()
                .issuer(PROPERTIES.issuer())
                .subject("42")
                .audience(List.of("bankingApp"))
                .id("token-id")
                .issuedAt(NOW.minusSeconds(10))
                .notBefore(NOW.minusSeconds(10))
                .expiresAt(NOW.plusSeconds(60))
                .claim("user_id", 42L)
                .claim("role", "USER")
                .claim("token_use", "access");
    }

    private String sign(JwtClaimsSet.Builder claims) {
        return encoder.encode(JwtEncoderParameters.from(HEADER, claims.build())).getTokenValue();
    }
}

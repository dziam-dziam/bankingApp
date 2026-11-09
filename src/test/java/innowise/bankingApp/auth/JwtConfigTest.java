package innowise.bankingApp.auth;

import innowise.bankingApp.auth.config.JwtConfig;
import innowise.bankingApp.auth.config.JwtProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.Base64;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtConfigTest {
    @ParameterizedTest
    @MethodSource("invalidConfiguration")
    void shouldRejectInvalidLifetimesAndMissingConfiguration(String secret, String issuer,
                                                             Duration access, Duration refresh) {
        assertThrows(IllegalArgumentException.class, () -> new JwtProperties(secret, issuer, access, refresh));
    }

    static Stream<Arguments> invalidConfiguration() {
        Duration access = Duration.ofMinutes(15);
        Duration refresh = Duration.ofDays(7);
        return Stream.of(
                Arguments.of(null, "bankingApp", access, refresh),
                Arguments.of("key", null, access, refresh),
                Arguments.of("key", " ", access, refresh),
                Arguments.of("key", "bankingApp", null, refresh),
                Arguments.of("key", "bankingApp", access, null),
                Arguments.of("key", "bankingApp", Duration.ZERO, refresh),
                Arguments.of("key", "bankingApp", Duration.ofSeconds(-1), refresh),
                Arguments.of("key", "bankingApp", access, access),
                Arguments.of("key", "bankingApp", access, Duration.ofMinutes(1)));
    }

    @Test
    void shouldRequireAtLeast256BitBase64SigningKey() {
        JwtConfig config = new JwtConfig();
        JwtProperties shortKey = properties(Base64.getEncoder().encodeToString(new byte[16]));
        assertThrows(IllegalArgumentException.class, () -> config.jwtSecretKey(shortKey));
        JwtProperties malformed = properties("not-base64!");
        assertThrows(IllegalArgumentException.class, () -> config.jwtSecretKey(malformed));
        JwtProperties valid = properties(Base64.getEncoder().encodeToString(new byte[32]));
        assertEquals(32, config.jwtSecretKey(valid).getEncoded().length);
        assertEquals("HmacSHA256", config.jwtSecretKey(valid).getAlgorithm());
    }

    private JwtProperties properties(String secret) {
        return new JwtProperties(secret, "bankingApp", Duration.ofMinutes(15), Duration.ofDays(7));
    }
}

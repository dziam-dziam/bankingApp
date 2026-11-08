package innowise.bankingApp.auth.security;

import innowise.bankingApp.auth.model.entity.CredentialsEntity;
import innowise.bankingApp.auth.repository.CredentialsRepository;
import innowise.bankingApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountJwtAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    private final CredentialsRepository credentialsRepository;
    private final UserRepository userRepository;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        if (!"access".equals(jwt.getClaimAsString("token_use"))) {
            throw new InvalidBearerTokenException("Invalid access token");
        }

        Long userId = Long.valueOf(jwt.getSubject());

        CredentialsEntity credentials = credentialsRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new InvalidBearerTokenException("Invalid access token")
                );

        if (!credentials.getRole().name().equals(jwt.getClaimAsString("role"))
                || !userRepository.existsByIdAndActiveTrue(userId)) {
            throw new InvalidBearerTokenException("Invalid access token");
        }

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                "ROLE_" + credentials.getRole().name()
        );

        return new JwtAuthenticationToken(jwt, List.of(authority));
    }
}
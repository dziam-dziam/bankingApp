package innowise.bankingApp.auth.service.impl;

import innowise.bankingApp.auth.model.dto.request.CreateCredentialsDto;
import innowise.bankingApp.auth.model.dto.request.LoginDto;
import innowise.bankingApp.auth.model.dto.response.ResponseCredentialsDto;
import innowise.bankingApp.auth.model.dto.response.ResponseTokenPairDto;
import innowise.bankingApp.auth.model.entity.CredentialsEntity;
import innowise.bankingApp.auth.repository.CredentialsRepository;
import innowise.bankingApp.auth.service.AuthService;
import innowise.bankingApp.auth.service.JwtTokenService;
import innowise.bankingApp.exception.UserNotFoundByIdException;
import innowise.bankingApp.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

@Service
@Validated
public class AuthServiceImpl implements AuthService {

    private final CredentialsRepository credentialsRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final String dummyPasswordHash;

    public AuthServiceImpl(
            CredentialsRepository credentialsRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService
    ) {
        this.credentialsRepository = credentialsRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.dummyPasswordHash = passwordEncoder.encode(
                UUID.randomUUID().toString()
        );
    }

    @Override
    @Transactional("authTransactionManager")
    public ResponseCredentialsDto saveCredentials(CreateCredentialsDto dto) {
        if (!userRepository.existsById(dto.userId())) {
            throw new UserNotFoundByIdException(dto.userId());
        }

        if (dto.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException(
                    "Password cannot exceed 72 UTF-8 bytes"
            );
        }

        CredentialsEntity credentials = new CredentialsEntity();
        credentials.setUserId(dto.userId());
        credentials.setLogin(normalizeLogin(dto.login()));
        credentials.setPasswordHash(passwordEncoder.encode(dto.password()));
        credentials.setRole(dto.role());

        CredentialsEntity saved = credentialsRepository.saveAndFlush(credentials);

        return new ResponseCredentialsDto(
                saved.getId(),
                saved.getUserId(),
                saved.getLogin(),
                saved.getRole()
        );
    }

    @Override
    @Transactional("authTransactionManager")
    public ResponseTokenPairDto login(LoginDto dto) {
        CredentialsEntity credentials = credentialsRepository
                .findByLoginForUpdate(normalizeLogin(dto.login()))
                .orElse(null);

        String storedHash = credentials == null
                ? dummyPasswordHash
                : credentials.getPasswordHash();

        boolean passwordMatches =
                dto.password().getBytes(StandardCharsets.UTF_8).length <= 72
                        && passwordEncoder.matches(dto.password(), storedHash);

        if (!passwordMatches || credentials == null) {
            throw new BadCredentialsException("Invalid credentials or token");
        }

        if (!userRepository.existsByIdAndActiveTrue(credentials.getUserId())) {
            throw new BadCredentialsException("Invalid credentials or token");
        }

        ResponseTokenPairDto tokens =
                jwtTokenService.generateTokens(credentials);

        credentials.setRefreshTokenHash(
                jwtTokenService.hashRefreshToken(tokens.refreshToken())
        );

        return tokens;
    }

    private static String normalizeLogin(String login) {
        return login.trim().toLowerCase(Locale.ROOT);
    }
}
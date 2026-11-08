package innowise.bankingApp.auth.service.impl;

import innowise.bankingApp.auth.model.dto.request.CreateCredentialsDto;
import innowise.bankingApp.auth.model.dto.response.ResponseCredentialsDto;
import innowise.bankingApp.auth.model.entity.CredentialsEntity;
import innowise.bankingApp.auth.repository.CredentialsRepository;
import innowise.bankingApp.auth.service.AuthService;
import innowise.bankingApp.exceptions.UserNotFoundByIdException;
import innowise.bankingApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
@Validated
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final CredentialsRepository credentialsRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
        credentials.setLogin(dto.login().toLowerCase(Locale.ROOT));
        credentials.setPasswordHash(passwordEncoder.encode(dto.password()));
        credentials.setRole(dto.role());

        CredentialsEntity saved =
                credentialsRepository.saveAndFlush(credentials);

        return new ResponseCredentialsDto(
                saved.getId(),
                saved.getUserId(),
                saved.getLogin(),
                saved.getRole()
        );
    }
}
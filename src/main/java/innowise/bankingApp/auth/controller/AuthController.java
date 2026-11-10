package innowise.bankingApp.auth.controller;

import innowise.bankingApp.auth.model.Role;
import innowise.bankingApp.auth.model.dto.request.CreateCredentialsDto;
import innowise.bankingApp.auth.model.dto.request.LoginDto;
import innowise.bankingApp.auth.model.dto.request.RefreshTokenDto;
import innowise.bankingApp.auth.model.dto.response.ResponseCredentialsDto;
import innowise.bankingApp.auth.model.dto.response.ResponseTokenPairDto;
import innowise.bankingApp.auth.model.dto.response.ResponseTokenValidationDto;
import innowise.bankingApp.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ResponseTokenPairDto> login(
            @Valid @RequestBody LoginDto dto
    ) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(authService.login(dto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseTokenPairDto> refresh(
            @Valid @RequestBody RefreshTokenDto dto
    ) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(authService.refresh(dto));
    }

    @GetMapping("/validate")
    public ResponseEntity<ResponseTokenValidationDto> validate(
            @AuthenticationPrincipal Jwt jwt
    ) {
        ResponseTokenValidationDto response =
                new ResponseTokenValidationDto(
                        true,
                        Long.valueOf(jwt.getSubject()),
                        Role.valueOf(jwt.getClaimAsString("role")),
                        jwt.getExpiresAt()
                );

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(response);
    }

    @PostMapping("/credentials")
    public ResponseEntity<ResponseCredentialsDto> saveCredentials(
            @Valid @RequestBody CreateCredentialsDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.saveCredentials(dto));
    }
}
package innowise.bankingApp.auth.service;

import innowise.bankingApp.auth.model.dto.request.CreateCredentialsDto;
import innowise.bankingApp.auth.model.dto.request.LoginDto;
import innowise.bankingApp.auth.model.dto.request.RefreshTokenDto;
import innowise.bankingApp.auth.model.dto.response.ResponseCredentialsDto;
import innowise.bankingApp.auth.model.dto.response.ResponseTokenPairDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface AuthService {

    ResponseCredentialsDto saveCredentials(
            @NotNull @Valid CreateCredentialsDto dto
    );

    ResponseTokenPairDto login(
            @NotNull @Valid LoginDto dto
    );

    ResponseTokenPairDto refresh(
            @NotNull @Valid RefreshTokenDto dto
    );
}
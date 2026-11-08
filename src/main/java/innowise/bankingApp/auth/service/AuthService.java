package innowise.bankingApp.auth.service;

import innowise.bankingApp.auth.model.dto.request.CreateCredentialsDto;
import innowise.bankingApp.auth.model.dto.response.ResponseCredentialsDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface AuthService {

    ResponseCredentialsDto saveCredentials(
            @NotNull @Valid CreateCredentialsDto dto
    );
}
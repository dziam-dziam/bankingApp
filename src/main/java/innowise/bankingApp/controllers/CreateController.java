package innowise.bankingApp.controllers;

import innowise.bankingApp.model.dtos.CreateDtos.CreatePaymentCardDto;
import innowise.bankingApp.model.dtos.CreateDtos.CreateUserDto;
import innowise.bankingApp.model.dtos.ResponseDtos.ResponsePaymentCardDto;
import innowise.bankingApp.model.dtos.ResponseDtos.ResponseUserDto;
import innowise.bankingApp.services.CreateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/create")
public class CreateController {
    private final CreateService createService;

    @PostMapping("/newUser")
    public ResponseEntity<ResponseUserDto> createNewUser(
            @Valid @RequestBody CreateUserDto createUserDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createService.createNewUser(createUserDto));
    }

    @PostMapping("/newPaymentCard")
    public ResponseEntity<ResponsePaymentCardDto> createNewPaymentCard(
            @Valid @RequestBody CreatePaymentCardDto createPaymentCardDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createService.createNewPaymentCard(createPaymentCardDto));
    }

}

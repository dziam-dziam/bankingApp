package innowise.bankingApp.controllers;

import innowise.bankingApp.model.dto.request.CreatePaymentCardDto;
import innowise.bankingApp.model.dto.request.CreateUserDto;
import innowise.bankingApp.model.dto.response.ResponsePaymentCardDto;
import innowise.bankingApp.model.dto.response.ResponseUserDto;
import innowise.bankingApp.services.CardService;
import innowise.bankingApp.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/create")
public class CreateController {

    private final UserService userService;
    private final CardService cardService;

    @PostMapping("/newUser")
    public ResponseEntity<ResponseUserDto> createNewUser(@Valid @RequestBody CreateUserDto createUserDto) {
        return ResponseEntity.ok(userService.createNewUser(createUserDto));
    }

    @PostMapping("/newPaymentCard")
    public ResponseEntity<ResponsePaymentCardDto> createNewPaymentCard(@Valid @RequestBody CreatePaymentCardDto createPaymentCardDto) {
        return ResponseEntity.ok(cardService.createNewPaymentCard(createPaymentCardDto));
    }
}
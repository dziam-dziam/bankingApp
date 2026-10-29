package innowise.bankingApp.controllers;

import innowise.bankingApp.model.dto.response.ResponsePaymentCardDto;
import innowise.bankingApp.model.dto.response.ResponseUserDto;
import innowise.bankingApp.model.dto.request.UpdatePaymentCardDto;
import innowise.bankingApp.model.dto.request.UpdateUserDto;
import innowise.bankingApp.services.CardService;
import innowise.bankingApp.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/update")
@RequiredArgsConstructor
public class UpdateController {

    private final UserService userService;
    private final CardService cardService;

    @PutMapping("/user/{userId}")
    public ResponseEntity<ResponseUserDto> updateUser(@PathVariable Long userId, @Valid @RequestBody UpdateUserDto updateUserDto) {
        return ResponseEntity.ok(userService.updateUser(userId, updateUserDto));
    }

    @PutMapping("/paymentCard/{cardId}")
    public ResponseEntity<ResponsePaymentCardDto> updatePaymentCard(@PathVariable Long cardId, @Valid @RequestBody UpdatePaymentCardDto updatePaymentCardDto) {
        return ResponseEntity.ok(cardService.updatePaymentCard(cardId, updatePaymentCardDto));
    }
}
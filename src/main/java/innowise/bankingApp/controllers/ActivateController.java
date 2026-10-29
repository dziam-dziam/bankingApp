package innowise.bankingApp.controllers;

import innowise.bankingApp.model.dto.response.ResponsePaymentCardDto;
import innowise.bankingApp.model.dto.response.ResponseUserDto;
import innowise.bankingApp.services.CardService;
import innowise.bankingApp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/activate")
public class ActivateController {

    private final UserService userService;
    private final CardService cardService;

    @PutMapping("/paymentCard/{cardId}")
    public ResponseEntity<ResponsePaymentCardDto> activatePaymentCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.activatePaymentCard(cardId));
    }

    @PutMapping("user/{userId}")
    public ResponseEntity<ResponseUserDto> activateUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.activateUser(userId));
    }
}
package innowise.bankingApp.controllers;

import innowise.bankingApp.model.dto.response.ResponsePaymentCardDto;
import innowise.bankingApp.model.dto.response.ResponseUserDto;
import innowise.bankingApp.services.CardService;
import innowise.bankingApp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deactivate")
public class DeactivateController {

    private final UserService userService;
    private final CardService cardService;

    @Transactional
    @PutMapping("/paymentCard/{cardId}")
    public ResponseEntity<ResponsePaymentCardDto> deactivatePaymentCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.deactivatePaymentCard(cardId));
    }

    @Transactional
    @PutMapping("user/{userId}")
    public ResponseEntity<ResponseUserDto> deactivateUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.deactivateUser(userId));
    }
}
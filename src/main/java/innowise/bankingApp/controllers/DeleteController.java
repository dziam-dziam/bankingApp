package innowise.bankingApp.controllers;

import innowise.bankingApp.services.CardService;
import innowise.bankingApp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/delete")
@RequiredArgsConstructor
public class DeleteController {

    private final UserService userService;
    private final CardService cardService;

    @DeleteMapping("user/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("paymentCard/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        cardService.deletePaymentCard(cardId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/allCards")
    public ResponseEntity<Void> deleteAllCards() {
        cardService.deleteAllPaymentCards();

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/allUsers")
    public ResponseEntity<Void> deleteAllUsers() {
        userService.deleteAllUsers();

        return ResponseEntity.noContent().build();
    }
}
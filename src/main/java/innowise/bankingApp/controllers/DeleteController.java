package innowise.bankingApp.controllers;

import innowise.bankingApp.services.DeleteService;

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

    private final DeleteService deleteService;

    @DeleteMapping("user/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        deleteService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("paymentCard/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        deleteService.deletePaymentCard(cardId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/allCards")
    public ResponseEntity<Void> deleteAllCards() {
        deleteService.deleteAllPaymentCards();

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/allUsers")
    public ResponseEntity<Void> deleteAllUsers() {
        deleteService.deleteAllUsers();

        return ResponseEntity.noContent().build();
    }
}

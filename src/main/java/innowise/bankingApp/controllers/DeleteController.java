package innowise.bankingApp.controllers;

import innowise.bankingApp.services.DeleteService;
import lombok.RequiredArgsConstructor;
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
    public void deleteUser(@PathVariable Long userId){
        deleteService.deleteUser(userId);
    }
    @DeleteMapping("paymentCard/{cardId}")
    public void deleteCard(@PathVariable Long cardId){
        deleteService.deletePaymentCard(cardId);
    }

    @DeleteMapping("/allCards")
    public void deleteAllCards(){
        deleteService.deleteAllPaymentCards();
    }

    @DeleteMapping("/allUsers")
    public void deleteAllUsers(){
        deleteService.deleteAllUsers();
    }
}

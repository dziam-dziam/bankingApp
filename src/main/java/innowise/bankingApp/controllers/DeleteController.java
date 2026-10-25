package innowise.bankingApp.controllers;

import innowise.bankingApp.repositories.PaymentCardRepository;
import innowise.bankingApp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/delete")
@RequiredArgsConstructor
public class DeleteController {

    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;

    @DeleteMapping("user/{userId}")
    public void deleteUser(@PathVariable Long userId){
        userRepository.deleteById(userId);
    }
    @DeleteMapping("paymentCard/{cardId}")
    public void deleteCard(@PathVariable Long cardId){
        paymentCardRepository.deleteById(cardId);
    }

    @DeleteMapping("/allCards")
    public void deleteAllCards(){
        paymentCardRepository.deleteAll();
    }

    @DeleteMapping("/allUsers")
    public void deleteAllUsers(){
        userRepository.deleteAll();
    }
}

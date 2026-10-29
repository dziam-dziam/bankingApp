package innowise.bankingApp.controllers;

import innowise.bankingApp.model.dto.response.ResponsePaymentCardDto;
import innowise.bankingApp.model.dto.response.ResponseUserDto;
import innowise.bankingApp.services.CardService;
import innowise.bankingApp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/get")
public class GetController {

    private final UserService userService;
    private final CardService cardService;

    @GetMapping("/user/{id}")
    public ResponseEntity<ResponseUserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/paymentCard/{id}")
    public ResponseEntity<ResponsePaymentCardDto> getPaymentCardDto(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getPaymentCard(id));
    }

    @GetMapping("/allUsers")
    public ResponseEntity<Page<ResponseUserDto>> getAllUsers(@RequestParam(required = false) String name, @RequestParam(required = false) String surname, Pageable pageable) {
        Page<ResponseUserDto> users = userService.getAllUsers(name, surname, pageable);

        return ResponseEntity.ok(users);
    }

    @GetMapping("/allCards")
    public ResponseEntity<Page<ResponsePaymentCardDto>> getAllPaymentCards(@RequestParam(required = false) String holderName, @RequestParam(required = false) String holderSurname, Pageable pageable) {
        Page<ResponsePaymentCardDto> paymentCards = cardService.getAllPaymentCards(holderName, holderSurname, pageable);

        return ResponseEntity.ok(paymentCards);
    }

    @GetMapping("allCardsByUser/{id}")
    public ResponseEntity<List<ResponsePaymentCardDto>> getAllPaymentCardsByUserId(@PathVariable Long id) {
        List<ResponsePaymentCardDto> cards = cardService.getAllPaymentCardsByUserId(id);

        return ResponseEntity.ok(cards);
    }
}
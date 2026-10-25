package innowise.bankingApp.controllers;

import innowise.bankingApp.model.dtos.ResponseDtos.ResponsePaymentCardDto;
import innowise.bankingApp.model.dtos.ResponseDtos.ResponseUserDto;
import innowise.bankingApp.services.GetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/get")
public class GetController {

    private final GetService getService;

    @GetMapping("/user/{id}")
    public ResponseEntity<ResponseUserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(getService.getUser(id));
    }

    @GetMapping("/paymentCard/{id}")
    public ResponseEntity<ResponsePaymentCardDto> getPaymentCardDto(@PathVariable Long id) {
        return ResponseEntity.ok(getService.getPaymentCard(id));
    }

    @GetMapping("/allUsers")
    public ResponseEntity<Page<ResponseUserDto>> getAllUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            Pageable pageable) {
        Page<ResponseUserDto> users = getService.getAllUsers(name, surname, pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/allCards")
    public ResponseEntity<Page<ResponsePaymentCardDto>> getAllPaymentCards(
            @RequestParam(required = false) String holderName,
            @RequestParam(required = false) String holderSurname,
            Pageable pageable){
        Page<ResponsePaymentCardDto> paymentCards = getService.getAllPaymentCards(holderName, holderSurname, pageable);
        return ResponseEntity.ok(paymentCards);
    }

    @GetMapping("allCardsByUser/{id}")
    public ResponseEntity<List<ResponsePaymentCardDto>> getAllPaymentCardsByUserId(
            @PathVariable Long id){
        List<ResponsePaymentCardDto> cards = getService.getAllPaymentCardsByUserId(id);
        return ResponseEntity.ok(cards);
    }


}

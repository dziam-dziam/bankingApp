package innowise.bankingApp.controllers;

import innowise.bankingApp.model.dto.request.CreatePaymentCardDto;
import innowise.bankingApp.model.dto.request.UpdatePaymentCardDto;
import innowise.bankingApp.model.dto.response.ResponsePaymentCardDto;
import innowise.bankingApp.services.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/paymentCard")
public class PaymentCardController {

    private final CardService cardService;

    @PostMapping("/create")
    public ResponseEntity<ResponsePaymentCardDto> createNewPaymentCard(@Valid @RequestBody CreatePaymentCardDto createPaymentCardDto) {
        return ResponseEntity.ok(cardService.createNewPaymentCard(createPaymentCardDto));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponsePaymentCardDto> getPaymentCardDto(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getPaymentCard(id));
    }

    @GetMapping("/getAll")
    public ResponseEntity<Page<ResponsePaymentCardDto>> getAllPaymentCards(@RequestParam(required = false) String holderName, @RequestParam(required = false) String holderSurname, Pageable pageable) {
        Page<ResponsePaymentCardDto> paymentCards = cardService.getAllPaymentCards(holderName, holderSurname, pageable);

        return ResponseEntity.ok(paymentCards);
    }

    @GetMapping("getAllById/{id}")
    public ResponseEntity<List<ResponsePaymentCardDto>> getAllPaymentCardsByUserId(@PathVariable Long id) {
        List<ResponsePaymentCardDto> cards = cardService.getAllPaymentCardsByUserId(id);

        return ResponseEntity.ok(cards);
    }

    @DeleteMapping("/delete/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        cardService.deletePaymentCard(cardId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{cardId}")
    public ResponseEntity<ResponsePaymentCardDto> updatePaymentCard(@PathVariable Long cardId, @Valid @RequestBody UpdatePaymentCardDto updatePaymentCardDto) {
        return ResponseEntity.ok(cardService.updatePaymentCard(cardId, updatePaymentCardDto));
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> deleteAllCards() {
        cardService.deleteAllPaymentCards();

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/activate/{cardId}")
    public ResponseEntity<ResponsePaymentCardDto> activatePaymentCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.activatePaymentCard(cardId));
    }

    @Transactional
    @PutMapping("/deactivate/{cardId}")
    public ResponseEntity<ResponsePaymentCardDto> deactivatePaymentCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.deactivatePaymentCard(cardId));
    }
}

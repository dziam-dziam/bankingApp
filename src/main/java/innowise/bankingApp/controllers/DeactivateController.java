package innowise.bankingApp.controllers;


import innowise.bankingApp.model.dtos.ResponseDtos.ResponsePaymentCardDto;
import innowise.bankingApp.model.dtos.ResponseDtos.ResponseUserDto;
import innowise.bankingApp.services.DeActivateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deactivate")
public class DeactivateController {

    private final DeActivateService deActivateService;

    @PutMapping("/paymentCard/{cardId}")
    public ResponseEntity<ResponsePaymentCardDto> deactivatePaymentCard(
            @PathVariable Long cardId
    ){
        return ResponseEntity.ok(deActivateService.deactivatePaymentCard(cardId));
    }

    @PutMapping("user/{userId}")
    public ResponseEntity<ResponseUserDto> deactivateUser(
            @PathVariable Long userId
    ){
        return ResponseEntity.ok(deActivateService.deactivateUser(userId));
    }
}

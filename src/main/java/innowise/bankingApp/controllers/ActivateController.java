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
@RequestMapping("/bank/activate")
public class ActivateController {

    private final DeActivateService deActivateService;

    @PutMapping("/paymentCard/{id}")
    public ResponseEntity<ResponsePaymentCardDto> activatePaymentCard(
            @PathVariable Long cardId
    ){
        return ResponseEntity.ok(deActivateService.activatePaymentCard(cardId));
    }

    @PutMapping("user/{id}")
    public ResponseEntity<ResponseUserDto> activateUser(
            @PathVariable Long userId
    ){
        return ResponseEntity.ok(deActivateService.activateUser(userId));
    }
}

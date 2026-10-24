package innowise.bankingApp.controllers;

import innowise.bankingApp.model.dtos.ResponseDtos.ResponsePaymentCardDto;
import innowise.bankingApp.model.dtos.ResponseDtos.ResponseUserDto;
import innowise.bankingApp.model.dtos.UpdateDtos.UpdatePaymentCardDto;
import innowise.bankingApp.model.dtos.UpdateDtos.UpdateUserDto;
import innowise.bankingApp.services.UpdateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bank/update")
@RequiredArgsConstructor
public class UpdateController {

    private final UpdateService updateService;

    @PutMapping("/{id}")
    public ResponseEntity<ResponseUserDto> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserDto updateUserDto){
        return ResponseEntity.ok(updateService.updateUser(userId,updateUserDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponsePaymentCardDto> updatePaymentCard(
            @PathVariable Long paymentCardId,
            @Valid @RequestBody UpdatePaymentCardDto updatePaymentCardDto){
        return ResponseEntity.ok(updateService.updatePaymentCard(paymentCardId,updatePaymentCardDto));
    }
}

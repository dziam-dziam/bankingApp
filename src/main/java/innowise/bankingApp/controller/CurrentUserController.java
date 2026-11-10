package innowise.bankingApp.controller;

import innowise.bankingApp.model.dto.request.CreateOwnPaymentCardDto;
import innowise.bankingApp.model.dto.request.UpdateOwnPaymentCardDto;
import innowise.bankingApp.model.dto.request.UpdateUserDto;
import innowise.bankingApp.model.dto.response.ResponsePaymentCardDto;
import innowise.bankingApp.model.dto.response.ResponseUserDto;
import innowise.bankingApp.model.dto.response.ResponseUserWithCardsDto;
import innowise.bankingApp.service.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me")
public class CurrentUserController {

    private final CurrentUserService currentUserService;

    @GetMapping
    public ResponseEntity<ResponseUserDto> getUser(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(currentUserService.getUser(userId(jwt)));
    }

    @GetMapping("/with-cards")
    public ResponseEntity<ResponseUserWithCardsDto> getUserWithCards(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                currentUserService.getUserWithCards(userId(jwt))
        );
    }

    @PutMapping
    public ResponseEntity<ResponseUserDto> updateUser(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateUserDto dto
    ) {
        return ResponseEntity.ok(
                currentUserService.updateUser(userId(jwt), dto)
        );
    }

    @GetMapping("/cards")
    public ResponseEntity<List<ResponsePaymentCardDto>> getCards(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(currentUserService.getCards(userId(jwt)));
    }

    @PostMapping("/cards")
    public ResponseEntity<ResponsePaymentCardDto> createCard(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateOwnPaymentCardDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(currentUserService.createCard(userId(jwt), dto));
    }

    @GetMapping("/cards/{cardId}")
    public ResponseEntity<ResponsePaymentCardDto> getCard(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long cardId
    ) {
        return ResponseEntity.ok(
                currentUserService.getCard(userId(jwt), cardId)
        );
    }

    @PutMapping("/cards/{cardId}")
    public ResponseEntity<ResponsePaymentCardDto> updateCard(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long cardId,
            @Valid @RequestBody UpdateOwnPaymentCardDto dto
    ) {
        return ResponseEntity.ok(
                currentUserService.updateCard(userId(jwt), cardId, dto)
        );
    }

    @DeleteMapping("/cards/{cardId}")
    public ResponseEntity<Void> deleteCard(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long cardId
    ) {
        currentUserService.deleteCard(userId(jwt), cardId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/cards/{cardId}/activate")
    public ResponseEntity<ResponsePaymentCardDto> activateCard(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long cardId
    ) {
        return ResponseEntity.ok(
                currentUserService.activateCard(userId(jwt), cardId)
        );
    }

    @PutMapping("/cards/{cardId}/deactivate")
    public ResponseEntity<ResponsePaymentCardDto> deactivateCard(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long cardId
    ) {
        return ResponseEntity.ok(
                currentUserService.deactivateCard(userId(jwt), cardId)
        );
    }

    private static Long userId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
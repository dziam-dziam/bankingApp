package innowise.bankingApp.service;

import innowise.bankingApp.model.dto.request.CreateOwnPaymentCardDto;
import innowise.bankingApp.model.dto.request.UpdateOwnPaymentCardDto;
import innowise.bankingApp.model.dto.request.UpdateUserDto;
import innowise.bankingApp.model.dto.response.ResponsePaymentCardDto;
import innowise.bankingApp.model.dto.response.ResponseUserDto;
import innowise.bankingApp.model.dto.response.ResponseUserWithCardsDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface CurrentUserService {

    ResponseUserDto getUser(Long userId);

    ResponseUserWithCardsDto getUserWithCards(Long userId);

    ResponseUserDto updateUser(
            Long userId,
            @NotNull @Valid UpdateUserDto dto
    );

    List<ResponsePaymentCardDto> getCards(Long userId);

    ResponsePaymentCardDto createCard(
            Long userId,
            @NotNull @Valid CreateOwnPaymentCardDto dto
    );

    ResponsePaymentCardDto getCard(Long userId, Long cardId);

    ResponsePaymentCardDto updateCard(
            Long userId,
            Long cardId,
            @NotNull @Valid UpdateOwnPaymentCardDto dto
    );

    void deleteCard(Long userId, Long cardId);

    ResponsePaymentCardDto activateCard(Long userId, Long cardId);

    ResponsePaymentCardDto deactivateCard(Long userId, Long cardId);
}
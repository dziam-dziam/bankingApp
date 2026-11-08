package innowise.bankingApp.service.impl;

import innowise.bankingApp.mapper.PaymentCardMapper;
import innowise.bankingApp.model.dto.request.CreateOwnPaymentCardDto;
import innowise.bankingApp.model.dto.request.CreatePaymentCardDto;
import innowise.bankingApp.model.dto.request.UpdateOwnPaymentCardDto;
import innowise.bankingApp.model.dto.request.UpdatePaymentCardDto;
import innowise.bankingApp.model.dto.request.UpdateUserDto;
import innowise.bankingApp.model.dto.response.ResponsePaymentCardDto;
import innowise.bankingApp.model.dto.response.ResponseUserDto;
import innowise.bankingApp.model.dto.response.ResponseUserWithCardsDto;
import innowise.bankingApp.model.entity.PaymentCardEntity;
import innowise.bankingApp.repository.PaymentCardRepository;
import innowise.bankingApp.service.CardService;
import innowise.bankingApp.service.CurrentUserService;
import innowise.bankingApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(value = "transactionManager", readOnly = true)
@PreAuthorize(
        "hasRole('ADMIN') or "
                + "(hasRole('USER') and #userId != null "
                + "and #userId.toString() == authentication.name)"
)
public class CurrentUserServiceImpl implements CurrentUserService {

    private final UserService userService;
    private final CardService cardService;
    private final PaymentCardRepository paymentCardRepository;
    private final PaymentCardMapper paymentCardMapper;

    @Override
    public ResponseUserDto getUser(Long userId) {
        return userService.getUser(userId);
    }

    @Override
    public ResponseUserWithCardsDto getUserWithCards(Long userId) {
        return userService.getUserWithCards(userId);
    }

    @Override
    @Transactional("transactionManager")
    public ResponseUserDto updateUser(Long userId, UpdateUserDto dto) {
        return userService.updateUser(userId, dto);
    }

    @Override
    public List<ResponsePaymentCardDto> getCards(Long userId) {
        return cardService.getAllPaymentCardsByUserId(userId);
    }

    @Override
    @Transactional("transactionManager")
    public ResponsePaymentCardDto createCard(
            Long userId,
            CreateOwnPaymentCardDto dto
    ) {
        return cardService.createNewPaymentCard(new CreatePaymentCardDto(
                userId,
                dto.number(),
                dto.holder(),
                dto.expirationDate()
        ));
    }

    @Override
    public ResponsePaymentCardDto getCard(Long userId, Long cardId) {
        PaymentCardEntity card = paymentCardRepository
                .findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new AccessDeniedException("Access denied"));

        return paymentCardMapper.toDto(card);
    }

    @Override
    @Transactional("transactionManager")
    public ResponsePaymentCardDto updateCard(
            Long userId,
            Long cardId,
            UpdateOwnPaymentCardDto dto
    ) {
        requireOwnedCardForUpdate(userId, cardId);

        return cardService.updatePaymentCard(
                cardId,
                new UpdatePaymentCardDto(
                        cardId,
                        dto.number(),
                        dto.holder(),
                        dto.expirationDate()
                )
        );
    }

    @Override
    @Transactional("transactionManager")
    public void deleteCard(Long userId, Long cardId) {
        requireOwnedCardForUpdate(userId, cardId);
        cardService.deletePaymentCard(cardId);
    }

    @Override
    @Transactional("transactionManager")
    public ResponsePaymentCardDto activateCard(Long userId, Long cardId) {
        requireOwnedCardForUpdate(userId, cardId);
        return cardService.activatePaymentCard(cardId);
    }

    @Override
    @Transactional("transactionManager")
    public ResponsePaymentCardDto deactivateCard(Long userId, Long cardId) {
        requireOwnedCardForUpdate(userId, cardId);
        return cardService.deactivatePaymentCard(cardId);
    }

    private void requireOwnedCardForUpdate(Long userId, Long cardId) {
        paymentCardRepository.findOwnedByIdForUpdate(cardId, userId)
                .orElseThrow(() -> new AccessDeniedException("Access denied"));
    }
}
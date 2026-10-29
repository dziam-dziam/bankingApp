package innowise.bankingApp.services;

import innowise.bankingApp.exceptions.ErrorCode;
import innowise.bankingApp.exceptions.PaymentCardAlreadyActiveException;
import innowise.bankingApp.exceptions.PaymentCardAlreadyDeactivatedException;
import innowise.bankingApp.exceptions.PaymentCardNotFoundByIdException;
import innowise.bankingApp.exceptions.UserNotFoundByIdException;
import innowise.bankingApp.mappers.PaymentCardMapper;
import innowise.bankingApp.model.dto.request.CreatePaymentCardDto;
import innowise.bankingApp.model.dto.response.ResponsePaymentCardDto;
import innowise.bankingApp.model.dto.request.UpdatePaymentCardDto;
import innowise.bankingApp.model.entity.PaymentCard;
import innowise.bankingApp.model.entity.User;
import innowise.bankingApp.repositories.PaymentCardRepository;
import innowise.bankingApp.repositories.UserRepository;
import innowise.bankingApp.specifications.PaymentCardSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private final PaymentCardMapper paymentCardMapper;
    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponsePaymentCardDto createNewPaymentCard(CreatePaymentCardDto createDto) {
        if (createDto == null) {
            throw new IllegalArgumentException(ErrorCode.DTO_REQUIRED.formatMessage());
        }

        Long userId = createDto.userId();

        if (userId == null) {
            throw new IllegalArgumentException(ErrorCode.USER_ID_REQUIRED.formatMessage());
        }

        User user = userRepository.findByIdForUpdate(userId).orElseThrow(() -> new UserNotFoundByIdException(userId));

        PaymentCard newPaymentCard = paymentCardMapper.toEntity(createDto);
        newPaymentCard.setActive(true);
        user.addCard(newPaymentCard);
        PaymentCard savedCard = paymentCardRepository.save(newPaymentCard);

        return paymentCardMapper.toDto(savedCard);
    }

    public ResponsePaymentCardDto getPaymentCard(Long paymentCardId) {
        if (paymentCardId == null) {
            throw new IllegalArgumentException(ErrorCode.CARD_ID_REQUIRED.formatMessage());
        }

        PaymentCard paymentCard = paymentCardRepository.findById(paymentCardId).orElseThrow(() -> new PaymentCardNotFoundByIdException(paymentCardId));

        return paymentCardMapper.toDto(paymentCard);
    }

    @Transactional(readOnly = true)
    public Page<ResponsePaymentCardDto> getAllPaymentCards(String holderName, String holderSurname, Pageable pageable) {
        return paymentCardRepository.findAll(PaymentCardSpecifications.filter(holderName, holderSurname), pageable).map(paymentCardMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ResponsePaymentCardDto> getAllPaymentCardsByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException(ErrorCode.USER_ID_REQUIRED.formatMessage());
        }

        List<PaymentCard> paymentCards = paymentCardRepository.findAllByUserId(userId);
        return paymentCards.stream().map(paymentCardMapper::toDto).toList();
    }

    @Transactional
    public ResponsePaymentCardDto updatePaymentCard(Long cardId, UpdatePaymentCardDto updateDto) {
        if (cardId == null) {
            throw new IllegalArgumentException(ErrorCode.ID_REQUIRED.formatMessage());
        }
        if (updateDto == null) {
            throw new IllegalArgumentException(ErrorCode.UPDATE_DTO_REQUIRED.formatMessage());
        }

        PaymentCard paymentCard = paymentCardRepository.findById(cardId).orElseThrow(() -> new PaymentCardNotFoundByIdException(cardId));

        PaymentCard updatedCard = paymentCardMapper.updateEntity(updateDto, paymentCard);
        PaymentCard savedCard = paymentCardRepository.save(updatedCard);

        return paymentCardMapper.toDto(savedCard);
    }

    @Transactional
    public void deletePaymentCard(Long cardId) {
        PaymentCard card = paymentCardRepository.findById(cardId).orElseThrow(() -> new PaymentCardNotFoundByIdException(cardId));

        paymentCardRepository.delete(card);
    }

    @Transactional
    public void deleteAllPaymentCards() {
        paymentCardRepository.deleteAll();
    }

    @Transactional
    public ResponsePaymentCardDto activatePaymentCard(Long cardId) {
        if (cardId == null) {
            throw new IllegalArgumentException(ErrorCode.ID_REQUIRED.formatMessage());
        }
        PaymentCard paymentCard = paymentCardRepository.findById(cardId).orElseThrow(() -> new PaymentCardNotFoundByIdException(cardId));

        if (paymentCard.isActive()) {
            throw new PaymentCardAlreadyActiveException();
        }

        paymentCard.setActive(true);
        return paymentCardMapper.toDto(paymentCard);
    }

    @Transactional
    public ResponsePaymentCardDto deactivatePaymentCard(Long cardId) {
        if (cardId == null) {
            throw new IllegalArgumentException(ErrorCode.ID_REQUIRED.formatMessage());
        }
        PaymentCard paymentCard = paymentCardRepository.findById(cardId).orElseThrow(() -> new PaymentCardNotFoundByIdException(cardId));

        if (!paymentCard.isActive()) {
            throw new PaymentCardAlreadyDeactivatedException();
        }

        paymentCard.setActive(false);
        return paymentCardMapper.toDto(paymentCard);
    }
}
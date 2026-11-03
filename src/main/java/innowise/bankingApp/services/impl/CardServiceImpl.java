package innowise.bankingApp.services.impl;

import innowise.bankingApp.exceptions.ErrorCode;
import innowise.bankingApp.exceptions.PaymentCardAlreadyActiveException;
import innowise.bankingApp.exceptions.PaymentCardAlreadyDeactivatedException;
import innowise.bankingApp.exceptions.PaymentCardNotFoundByIdException;
import innowise.bankingApp.exceptions.UserNotFoundByIdException;
import innowise.bankingApp.mappers.PaymentCardMapper;
import innowise.bankingApp.model.dto.request.CreatePaymentCardDto;
import innowise.bankingApp.model.dto.request.UpdatePaymentCardDto;
import innowise.bankingApp.model.dto.response.ResponsePaymentCardDto;
import innowise.bankingApp.model.entity.PaymentCardEntity;
import innowise.bankingApp.model.entity.UserEntity;
import innowise.bankingApp.repositories.PaymentCardRepository;
import innowise.bankingApp.repositories.UserRepository;
import innowise.bankingApp.services.interfaces.CardService;
import innowise.bankingApp.specifications.PaymentCardSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final PaymentCardMapper paymentCardMapper;
    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;

    @Transactional
    @CacheEvict(cacheNames = "usersWithCards", key = "#result.userId()")
    public ResponsePaymentCardDto createNewPaymentCard(CreatePaymentCardDto createDto) {
        validateCreateArguments(createDto);

        return createNewPaymentCardAndReturnResponseDto(createDto);
    }

    private static void validateCreateArguments(CreatePaymentCardDto createDto) {
        validateIfNotNull(createDto, ErrorCode.DTO_REQUIRED);
        validateIfNotNull(createDto.userId(), ErrorCode.USER_ID_REQUIRED);
    }

    private ResponsePaymentCardDto createNewPaymentCardAndReturnResponseDto(CreatePaymentCardDto createDto) {
        UserEntity user = findUserForUpdate(createDto.userId());

        PaymentCardEntity newPaymentCard = paymentCardMapper.toEntity(createDto);
        newPaymentCard.setActive(true);
        user.addCard(newPaymentCard);
        PaymentCardEntity savedCard = paymentCardRepository.save(newPaymentCard);

        return paymentCardMapper.toDto(savedCard);
    }

    private UserEntity findUserForUpdate(Long userId) {
        return userRepository.findByIdForUpdate(userId).orElseThrow(() -> new UserNotFoundByIdException(userId));
    }

    public ResponsePaymentCardDto getPaymentCard(Long paymentCardId) {
        validateIfNotNull(paymentCardId, ErrorCode.CARD_ID_REQUIRED);

        return paymentCardMapper.toDto(findPaymentCardById(paymentCardId));
    }

    @Transactional(readOnly = true)
    public Page<ResponsePaymentCardDto> getAllPaymentCards(String holderName, String holderSurname, Pageable pageable) {
        return paymentCardRepository.findAll(PaymentCardSpecifications.filter(holderName, holderSurname), pageable).map(paymentCardMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ResponsePaymentCardDto> getAllPaymentCardsByUserId(Long userId) {
        validateIfNotNull(userId, ErrorCode.USER_ID_REQUIRED);

        return getPaymentCardsByUserIdAndReturnResponseDtos(userId);
    }

    private List<ResponsePaymentCardDto> getPaymentCardsByUserIdAndReturnResponseDtos(Long userId) {
        List<PaymentCardEntity> paymentCards = paymentCardRepository.findAllByUserId(userId);

        return paymentCards.stream().map(paymentCardMapper::toDto).toList();
    }

    @Transactional
    @CacheEvict(cacheNames = "usersWithCards", key = "#result.userId()")
    public ResponsePaymentCardDto updatePaymentCard(Long cardId, UpdatePaymentCardDto updateDto) {
        validateUpdateArguments(cardId, updateDto);

        return updatePaymentCardAndReturnResponseDto(cardId, updateDto);
    }

    private static void validateUpdateArguments(Long cardId, UpdatePaymentCardDto updateDto) {
        validateIfNotNull(cardId, ErrorCode.ID_REQUIRED);
        validateIfNotNull(updateDto, ErrorCode.UPDATE_DTO_REQUIRED);
    }

    private ResponsePaymentCardDto updatePaymentCardAndReturnResponseDto(Long cardId, UpdatePaymentCardDto updateDto) {
        PaymentCardEntity paymentCard = findPaymentCardById(cardId);

        PaymentCardEntity updatedCard = paymentCardMapper.updateEntity(updateDto, paymentCard);
        PaymentCardEntity savedCard = paymentCardRepository.save(updatedCard);

        return paymentCardMapper.toDto(savedCard);
    }

    @Transactional
    @CacheEvict(cacheNames = "usersWithCards", allEntries = true)
    public void deletePaymentCard(Long cardId) {
        PaymentCardEntity paymentCard = findPaymentCardById(cardId);

        paymentCardRepository.delete(paymentCard);
    }

    @Transactional
    @CacheEvict(cacheNames = "usersWithCards", allEntries = true)
    public void deleteAllPaymentCards() {
        paymentCardRepository.deleteAll();
    }

    @Transactional
    @CacheEvict(cacheNames = "usersWithCards", key = "#result.userId()")
    public ResponsePaymentCardDto activatePaymentCard(Long cardId) {
        validateIfNotNull(cardId, ErrorCode.ID_REQUIRED);
        PaymentCardEntity paymentCard = findPaymentCardAndCheckIfActive(cardId);

        paymentCard.setActive(true);
        return paymentCardMapper.toDto(paymentCard);
    }

    private PaymentCardEntity findPaymentCardAndCheckIfActive(Long cardId) {
        PaymentCardEntity paymentCard = findPaymentCardById(cardId);

        if (paymentCard.isActive()) {
            throw new PaymentCardAlreadyActiveException();
        }

        return paymentCard;
    }

    @Transactional
    @CacheEvict(cacheNames = "usersWithCards", key = "#result.userId()")
    public ResponsePaymentCardDto deactivatePaymentCard(Long cardId) {
        validateIfNotNull(cardId, ErrorCode.ID_REQUIRED);
        PaymentCardEntity paymentCard = findPaymentCardAndCheckIfDeactivated(cardId);

        paymentCard.setActive(false);
        return paymentCardMapper.toDto(paymentCard);
    }

    private PaymentCardEntity findPaymentCardAndCheckIfDeactivated(Long cardId) {
        PaymentCardEntity paymentCard = findPaymentCardById(cardId);

        if (!paymentCard.isActive()) {
            throw new PaymentCardAlreadyDeactivatedException();
        }

        return paymentCard;
    }

    private PaymentCardEntity findPaymentCardById(Long cardId) {
        return paymentCardRepository.findById(cardId).orElseThrow(() -> new PaymentCardNotFoundByIdException(cardId));
    }

    private static void validateIfNotNull(Object value, ErrorCode errorCode) {
        if (value == null) {
            throw new IllegalArgumentException(errorCode.formatMessage());
        }
    }
}

package innowise.bankingApp.service;

import innowise.bankingApp.exception.ErrorCode;
import innowise.bankingApp.exception.PaymentCardAlreadyActiveException;
import innowise.bankingApp.exception.PaymentCardAlreadyDeactivatedException;
import innowise.bankingApp.exception.PaymentCardNotFoundByIdException;
import innowise.bankingApp.exception.TooManyPaymentCardsException;
import innowise.bankingApp.exception.UserNotFoundByIdException;
import innowise.bankingApp.mapper.PaymentCardMapper;
import innowise.bankingApp.model.dto.request.CreatePaymentCardDto;
import innowise.bankingApp.model.dto.request.UpdatePaymentCardDto;
import innowise.bankingApp.model.dto.response.ResponsePaymentCardDto;
import innowise.bankingApp.model.entity.PaymentCardEntity;
import innowise.bankingApp.model.entity.UserEntity;
import innowise.bankingApp.repository.PaymentCardRepository;
import innowise.bankingApp.repository.UserRepository;
import innowise.bankingApp.service.impl.CardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private PaymentCardMapper paymentCardMapper;
    @Mock
    private PaymentCardRepository paymentCardRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CardServiceImpl cardService;

    private final LocalDate EXPIRATION_DATE = LocalDate.of(2030, 12, 31);

    @Test
    void shouldCreateNewPaymentCard() {
        //given
        Long userId = 1L;
        CreatePaymentCardDto createPaymentCardDto = new CreatePaymentCardDto(
                userId, "1111222233334444", "Damian Majkowski", EXPIRATION_DATE
        );
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        PaymentCardEntity paymentCardEntity = new PaymentCardEntity();
        paymentCardEntity.setNumber(createPaymentCardDto.number());
        paymentCardEntity.setHolder(createPaymentCardDto.holder());
        paymentCardEntity.setExpirationDate(createPaymentCardDto.expirationDate());
        paymentCardEntity.setActive(false);
        PaymentCardEntity savedPaymentCardEntity = new PaymentCardEntity();
        savedPaymentCardEntity.setId(2L);
        savedPaymentCardEntity.setNumber(createPaymentCardDto.number());
        savedPaymentCardEntity.setHolder(createPaymentCardDto.holder());
        savedPaymentCardEntity.setExpirationDate(createPaymentCardDto.expirationDate());
        savedPaymentCardEntity.setActive(true);
        savedPaymentCardEntity.assignUser(userEntity);
        ResponsePaymentCardDto expectedResponse = new ResponsePaymentCardDto(
                2L, userId, "************4444", "Damian Majkowski", EXPIRATION_DATE, true
        );
        when(userRepository.findByIdForUpdate(userId)).thenReturn(Optional.of(userEntity));
        when(paymentCardMapper.toEntity(createPaymentCardDto)).thenReturn(paymentCardEntity);
        when(paymentCardRepository.save(paymentCardEntity)).thenReturn(savedPaymentCardEntity);
        when(paymentCardMapper.toDto(savedPaymentCardEntity)).thenReturn(expectedResponse);

        //when
        ResponsePaymentCardDto result = cardService.createNewPaymentCard(createPaymentCardDto);

        //then
        assertSame(expectedResponse, result);
        assertTrue(paymentCardEntity.isActive());
        assertSame(userEntity, paymentCardEntity.getUser());
        assertEquals(1, userEntity.getPaymentCards().size());
        assertSame(paymentCardEntity, userEntity.getPaymentCards().get(0));
        verify(userRepository).findByIdForUpdate(userId);
        verify(paymentCardMapper).toEntity(createPaymentCardDto);
        verify(paymentCardRepository).save(paymentCardEntity);
        verify(paymentCardMapper).toDto(savedPaymentCardEntity);
    }

    @Test
    void shouldCreateNewPaymentCardWhenUserHasFourCards() {
        //given
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        for (int i = 0; i < 4; i++) {
            userEntity.addCard(new PaymentCardEntity());
        }
        CreatePaymentCardDto createPaymentCardDto = new CreatePaymentCardDto(
                userId, "1111222233334444", "Damian Majkowski", EXPIRATION_DATE
        );
        PaymentCardEntity paymentCardEntity = new PaymentCardEntity();
        paymentCardEntity.setNumber(createPaymentCardDto.number());
        paymentCardEntity.setHolder(createPaymentCardDto.holder());
        paymentCardEntity.setExpirationDate(createPaymentCardDto.expirationDate());
        paymentCardEntity.setActive(false);
        ResponsePaymentCardDto expectedResponse = new ResponsePaymentCardDto(
                2L, userId, "************4444", "Damian Majkowski", EXPIRATION_DATE, true
        );
        when(userRepository.findByIdForUpdate(userId)).thenReturn(Optional.of(userEntity));
        when(paymentCardMapper.toEntity(createPaymentCardDto)).thenReturn(paymentCardEntity);
        when(paymentCardRepository.save(paymentCardEntity)).thenReturn(paymentCardEntity);
        when(paymentCardMapper.toDto(paymentCardEntity)).thenReturn(expectedResponse);

        //when
        ResponsePaymentCardDto result = cardService.createNewPaymentCard(createPaymentCardDto);

        //then
        assertSame(expectedResponse, result);
        assertEquals(5, userEntity.getPaymentCards().size());
        assertSame(paymentCardEntity, userEntity.getPaymentCards().get(4));
        assertSame(userEntity, paymentCardEntity.getUser());
        assertTrue(paymentCardEntity.isActive());
        verify(paymentCardRepository).save(paymentCardEntity);
    }

    @Test
    void shouldNotCreateNewPaymentCardWhenCreatePaymentCardDtoIsNull() {
        //given
        CreatePaymentCardDto createPaymentCardDto = null;

        //when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> cardService.createNewPaymentCard(createPaymentCardDto)
        );

        //then
        assertEquals(ErrorCode.DTO_REQUIRED.formatMessage(), exception.getMessage());
        verifyNoInteractions(paymentCardMapper, paymentCardRepository, userRepository);
    }

    @Test
    void shouldNotCreateNewPaymentCardWhenUserIdIsNull() {
        //given
        CreatePaymentCardDto createPaymentCardDto = new CreatePaymentCardDto(
                null, "1111222233334444", "Damian Majkowski", EXPIRATION_DATE
        );

        //when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> cardService.createNewPaymentCard(createPaymentCardDto)
        );

        //then
        assertEquals(ErrorCode.USER_ID_REQUIRED.formatMessage(), exception.getMessage());
        verifyNoInteractions(paymentCardMapper, paymentCardRepository, userRepository);
    }

    @Test
    void shouldNotCreateNewPaymentCardWhenUserDoesNotExist() {
        //given
        Long userId = 1L;
        CreatePaymentCardDto createPaymentCardDto = new CreatePaymentCardDto(
                userId, "1111222233334444", "Damian Majkowski", EXPIRATION_DATE
        );
        when(userRepository.findByIdForUpdate(userId)).thenReturn(Optional.empty());

        //when
        UserNotFoundByIdException exception = assertThrows(
                UserNotFoundByIdException.class, () -> cardService.createNewPaymentCard(createPaymentCardDto)
        );

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND.formatMessage(userId), exception.getMessage());
        verify(userRepository).findByIdForUpdate(userId);
        verifyNoInteractions(paymentCardMapper, paymentCardRepository);
    }

    @Test
    void shouldNotCreateNewPaymentCardWhenUserHasFiveCards() {
        //given
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        for (int i = 0; i < 5; i++) {
            userEntity.addCard(new PaymentCardEntity());
        }
        CreatePaymentCardDto createPaymentCardDto = new CreatePaymentCardDto(
                userId, "1111222233334444", "Damian Majkowski", EXPIRATION_DATE
        );
        PaymentCardEntity paymentCardEntity = new PaymentCardEntity();
        when(userRepository.findByIdForUpdate(userId)).thenReturn(Optional.of(userEntity));
        when(paymentCardMapper.toEntity(createPaymentCardDto)).thenReturn(paymentCardEntity);

        //when
        TooManyPaymentCardsException exception = assertThrows(
                TooManyPaymentCardsException.class, () -> cardService.createNewPaymentCard(createPaymentCardDto)
        );

        //then
        assertEquals(ErrorCode.TOO_MANY_PAYMENT_CARDS.formatMessage(), exception.getMessage());
        assertEquals(5, userEntity.getPaymentCards().size());
        assertFalse(userEntity.getPaymentCards().contains(paymentCardEntity));
        assertNull(paymentCardEntity.getUser());
        verifyNoInteractions(paymentCardRepository);
        verify(paymentCardMapper, never()).toDto(any(PaymentCardEntity.class));
    }

    @Test
    void shouldGetPaymentCard() {
        //given
        Long paymentCardId = 2L;
        PaymentCardEntity paymentCardEntity = new PaymentCardEntity();
        paymentCardEntity.setId(paymentCardId);
        paymentCardEntity.setNumber("1111222233334444");
        paymentCardEntity.setHolder("Damian Majkowski");
        paymentCardEntity.setExpirationDate(EXPIRATION_DATE);
        paymentCardEntity.setActive(true);
        ResponsePaymentCardDto expectedResponse = new ResponsePaymentCardDto(
                paymentCardId, 1L, "************4444", "Damian Majkowski", EXPIRATION_DATE, true
        );
        when(paymentCardRepository.findById(paymentCardId)).thenReturn(Optional.of(paymentCardEntity));
        when(paymentCardMapper.toDto(paymentCardEntity)).thenReturn(expectedResponse);

        //when
        ResponsePaymentCardDto result = cardService.getPaymentCard(paymentCardId);

        //then
        assertSame(expectedResponse, result);
        verify(paymentCardRepository).findById(paymentCardId);
        verify(paymentCardMapper).toDto(paymentCardEntity);
    }

    @Test
    void shouldNotGetPaymentCardWhenPaymentCardIdIsNull() {
        //given
        Long paymentCardId = null;

        //when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> cardService.getPaymentCard(paymentCardId)
        );

        //then
        assertEquals(ErrorCode.CARD_ID_REQUIRED.formatMessage(), exception.getMessage());
        verifyNoInteractions(paymentCardRepository, paymentCardMapper, userRepository);
    }

    @Test
    void shouldNotGetPaymentCardWhenPaymentCardDoesNotExist() {
        //given
        Long paymentCardId = 2L;
        when(paymentCardRepository.findById(paymentCardId)).thenReturn(Optional.empty());

        //when
        PaymentCardNotFoundByIdException exception = assertThrows(
                PaymentCardNotFoundByIdException.class, () -> cardService.getPaymentCard(paymentCardId)
        );

        //then
        assertEquals(ErrorCode.PAYMENT_CARD_NOT_FOUND.formatMessage(paymentCardId), exception.getMessage());
        verify(paymentCardRepository).findById(paymentCardId);
        verifyNoInteractions(paymentCardMapper, userRepository);
    }

    @Test
    void shouldGetAllPaymentCards() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        PaymentCardEntity paymentCardEntity = new PaymentCardEntity();
        ResponsePaymentCardDto expectedResponse = new ResponsePaymentCardDto(
                2L, 1L, "************4444", "Damian Majkowski", EXPIRATION_DATE, true
        );
        Page<PaymentCardEntity> paymentCards = new PageImpl<>(List.of(paymentCardEntity), pageable, 1);
        when(paymentCardRepository.findAll(org.mockito.ArgumentMatchers.<Specification<PaymentCardEntity>>any(), eq(pageable)))
                .thenReturn(paymentCards);
        when(paymentCardMapper.toDto(paymentCardEntity)).thenReturn(expectedResponse);

        //when
        Page<ResponsePaymentCardDto> result = cardService.getAllPaymentCards(null, null, pageable);

        //then
        assertEquals(List.of(expectedResponse), result.getContent());
        assertEquals(1L, result.getTotalElements());
        assertEquals(pageable, result.getPageable());
        verify(paymentCardMapper).toDto(paymentCardEntity);
    }

    @Test
    void shouldGetAllPaymentCardsWithHolderNameAndHolderSurname() {
        //given
        String holderName = "Damian";
        String holderSurname = "Majkowski";
        Pageable pageable = PageRequest.of(1, 2);
        PaymentCardEntity paymentCardEntity = new PaymentCardEntity();
        ResponsePaymentCardDto expectedResponse = new ResponsePaymentCardDto(
                2L, 1L, "************4444", "Damian Majkowski", EXPIRATION_DATE, true
        );
        Page<PaymentCardEntity> paymentCards = new PageImpl<>(List.of(paymentCardEntity), pageable, 3);
        when(paymentCardRepository.findAll(org.mockito.ArgumentMatchers.<Specification<PaymentCardEntity>>any(), eq(pageable)))
                .thenReturn(paymentCards);
        when(paymentCardMapper.toDto(paymentCardEntity)).thenReturn(expectedResponse);

        //when
        Page<ResponsePaymentCardDto> result = cardService.getAllPaymentCards(holderName, holderSurname, pageable);

        //then
        assertEquals(List.of(expectedResponse), result.getContent());
        assertEquals(1, result.getNumber());
        assertEquals(2, result.getSize());
        assertEquals(3L, result.getTotalElements());
        verify(paymentCardMapper).toDto(paymentCardEntity);
    }

    @Test
    void shouldNotReturnPaymentCardsWhenPageIsEmpty() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        when(paymentCardRepository.findAll(org.mockito.ArgumentMatchers.<Specification<PaymentCardEntity>>any(), eq(pageable)))
                .thenReturn(Page.empty(pageable));

        //when
        Page<ResponsePaymentCardDto> result = cardService.getAllPaymentCards(null, null, pageable);

        //then
        assertTrue(result.isEmpty());
        assertEquals(0L, result.getTotalElements());
        assertEquals(pageable, result.getPageable());
        verifyNoInteractions(paymentCardMapper, userRepository);
    }

    @Test
    void shouldGetAllPaymentCardsByUserId() {
        //given
        Long userId = 1L;
        PaymentCardEntity paymentCardEntity = new PaymentCardEntity();
        paymentCardEntity.setId(2L);
        PaymentCardEntity secondPaymentCardEntity = new PaymentCardEntity();
        secondPaymentCardEntity.setId(3L);
        ResponsePaymentCardDto expectedResponse = new ResponsePaymentCardDto(
                2L, userId, "************4444", "Damian Majkowski", EXPIRATION_DATE, true
        );
        ResponsePaymentCardDto expectedSecondResponse = new ResponsePaymentCardDto(
                3L, userId, "************8888", "Damian Majkowski", EXPIRATION_DATE, true
        );
        when(paymentCardRepository.findAllByUserId(userId))
                .thenReturn(List.of(paymentCardEntity, secondPaymentCardEntity));
        when(paymentCardMapper.toDto(paymentCardEntity)).thenReturn(expectedResponse);
        when(paymentCardMapper.toDto(secondPaymentCardEntity)).thenReturn(expectedSecondResponse);

        //when
        List<ResponsePaymentCardDto> result = cardService.getAllPaymentCardsByUserId(userId);

        //then
        assertEquals(List.of(expectedResponse, expectedSecondResponse), result);
        verify(paymentCardRepository).findAllByUserId(userId);
        verify(paymentCardMapper).toDto(paymentCardEntity);
        verify(paymentCardMapper).toDto(secondPaymentCardEntity);
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldNotReturnPaymentCardsWhenUserHasNoCards() {
        //given
        Long userId = 1L;
        when(paymentCardRepository.findAllByUserId(userId)).thenReturn(List.of());

        //when
        List<ResponsePaymentCardDto> result = cardService.getAllPaymentCardsByUserId(userId);

        //then
        assertTrue(result.isEmpty());
        verify(paymentCardRepository).findAllByUserId(userId);
        verifyNoInteractions(paymentCardMapper, userRepository);
    }

    @Test
    void shouldNotGetAllPaymentCardsByUserIdWhenUserIdIsNull() {
        //given
        Long userId = null;

        //when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> cardService.getAllPaymentCardsByUserId(userId)
        );

        //then
        assertEquals(ErrorCode.USER_ID_REQUIRED.formatMessage(), exception.getMessage());
        verifyNoInteractions(paymentCardRepository, paymentCardMapper, userRepository);
    }

    @Test
    void shouldUpdatePaymentCard() {
        //given
        Long paymentCardId = 2L;
        PaymentCardEntity notUpdatedPaymentCardEntity = new PaymentCardEntity();
        notUpdatedPaymentCardEntity.setId(paymentCardId);
        notUpdatedPaymentCardEntity.setNumber("1111222233334444");
        notUpdatedPaymentCardEntity.setHolder("Damian Majkowski");
        notUpdatedPaymentCardEntity.setExpirationDate(EXPIRATION_DATE);
        notUpdatedPaymentCardEntity.setActive(true);
        UpdatePaymentCardDto updatePaymentCardDto = new UpdatePaymentCardDto(
                paymentCardId, "5555666677778888", "Maciek Markowiak", LocalDate.of(2031, 12, 31)
        );
        PaymentCardEntity updatedPaymentCardEntity = new PaymentCardEntity();
        updatedPaymentCardEntity.setId(paymentCardId);
        updatedPaymentCardEntity.setNumber(updatePaymentCardDto.number());
        updatedPaymentCardEntity.setHolder(updatePaymentCardDto.holder());
        updatedPaymentCardEntity.setExpirationDate(updatePaymentCardDto.expirationDate());
        updatedPaymentCardEntity.setActive(true);
        PaymentCardEntity savedPaymentCardEntity = new PaymentCardEntity();
        savedPaymentCardEntity.setId(paymentCardId);
        savedPaymentCardEntity.setNumber(updatePaymentCardDto.number());
        savedPaymentCardEntity.setHolder(updatePaymentCardDto.holder());
        savedPaymentCardEntity.setExpirationDate(updatePaymentCardDto.expirationDate());
        savedPaymentCardEntity.setActive(true);
        ResponsePaymentCardDto expectedResponse = new ResponsePaymentCardDto(
                paymentCardId, 1L, "************8888", "Maciek Markowiak", LocalDate.of(2031, 12, 31), true
        );
        when(paymentCardRepository.findById(paymentCardId)).thenReturn(Optional.of(notUpdatedPaymentCardEntity));
        when(paymentCardMapper.updateEntity(updatePaymentCardDto, notUpdatedPaymentCardEntity))
                .thenReturn(updatedPaymentCardEntity);
        when(paymentCardMapper.toDto(savedPaymentCardEntity)).thenReturn(expectedResponse);

        //when
        ResponsePaymentCardDto result = cardService.updatePaymentCard(paymentCardId, updatePaymentCardDto);

        //then
        assertSame(expectedResponse, result);
        verify(paymentCardRepository).findById(paymentCardId);
        verify(paymentCardMapper).updateEntity(updatePaymentCardDto, notUpdatedPaymentCardEntity);
        verify(paymentCardMapper).toDto(savedPaymentCardEntity);
    }

    @Test
    void shouldNotUpdatePaymentCardWhenPaymentCardIdIsNull() {
        //given
        Long paymentCardId = null;
        UpdatePaymentCardDto updatePaymentCardDto = new UpdatePaymentCardDto(
                2L, "5555666677778888", "Maciek Markowiak", EXPIRATION_DATE
        );

        //when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> cardService.updatePaymentCard(paymentCardId, updatePaymentCardDto)
        );

        //then
        assertEquals(ErrorCode.ID_REQUIRED.formatMessage(), exception.getMessage());
        verifyNoInteractions(paymentCardRepository, paymentCardMapper, userRepository);
    }

    @Test
    void shouldNotUpdatePaymentCardWhenUpdatePaymentCardDtoIsNull() {
        //given
        Long paymentCardId = 2L;
        UpdatePaymentCardDto updatePaymentCardDto = null;

        //when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> cardService.updatePaymentCard(paymentCardId, updatePaymentCardDto)
        );

        //then
        assertEquals(ErrorCode.UPDATE_DTO_REQUIRED.formatMessage(), exception.getMessage());
        verifyNoInteractions(paymentCardRepository, paymentCardMapper, userRepository);
    }

    @Test
    void shouldNotUpdatePaymentCardWhenPaymentCardDoesNotExist() {
        //given
        Long paymentCardId = 2L;
        UpdatePaymentCardDto updatePaymentCardDto = new UpdatePaymentCardDto(
                paymentCardId, "5555666677778888", "Maciek Markowiak", EXPIRATION_DATE
        );
        when(paymentCardRepository.findById(paymentCardId)).thenReturn(Optional.empty());

        //when
        PaymentCardNotFoundByIdException exception = assertThrows(
                PaymentCardNotFoundByIdException.class,
                () -> cardService.updatePaymentCard(paymentCardId, updatePaymentCardDto)
        );

        //then
        assertEquals(ErrorCode.PAYMENT_CARD_NOT_FOUND.formatMessage(paymentCardId), exception.getMessage());
        verify(paymentCardRepository, never()).save(any(PaymentCardEntity.class));
        verifyNoInteractions(paymentCardMapper, userRepository);
    }

    @Test
    void shouldDeletePaymentCard() {
        //given
        Long paymentCardId = 2L;
        UserEntity user = new UserEntity();
        user.setId(1L);
        PaymentCardEntity paymentCardEntity = new PaymentCardEntity();
        paymentCardEntity.setId(paymentCardId);
        paymentCardEntity.assignUser(user);
        when(paymentCardRepository.findById(paymentCardId)).thenReturn(Optional.of(paymentCardEntity));

        //when
        cardService.deletePaymentCard(paymentCardId);

        //then
        verify(paymentCardRepository).findById(paymentCardId);
        verify(paymentCardRepository).delete(paymentCardEntity);
        verifyNoInteractions(paymentCardMapper, userRepository);
    }

    @Test
    void shouldNotDeletePaymentCardWhenPaymentCardDoesNotExist() {
        //given
        Long paymentCardId = 2L;
        when(paymentCardRepository.findById(paymentCardId)).thenReturn(Optional.empty());

        //when
        PaymentCardNotFoundByIdException exception = assertThrows(
                PaymentCardNotFoundByIdException.class, () -> cardService.deletePaymentCard(paymentCardId)
        );

        //then
        assertEquals(ErrorCode.PAYMENT_CARD_NOT_FOUND.formatMessage(paymentCardId), exception.getMessage());
        verify(paymentCardRepository, never()).delete(any(PaymentCardEntity.class));
        verifyNoInteractions(paymentCardMapper, userRepository);
    }

    @Test
    void shouldDeleteAllPaymentCards() {
        //given
        // Brak argumentow i danych do przygotowania.

        //when
        cardService.deleteAllPaymentCards();

        //then
        verify(paymentCardRepository).deleteAll();
        verifyNoInteractions(paymentCardMapper, userRepository);
    }

    @Test
    void shouldActivatePaymentCard() {
        //given
        Long paymentCardId = 2L;
        PaymentCardEntity paymentCardEntity = new PaymentCardEntity();
        paymentCardEntity.setId(paymentCardId);
        paymentCardEntity.setNumber("1111222233334444");
        paymentCardEntity.setHolder("Damian Majkowski");
        paymentCardEntity.setExpirationDate(EXPIRATION_DATE);
        paymentCardEntity.setActive(false);
        ResponsePaymentCardDto expectedResponse = new ResponsePaymentCardDto(
                paymentCardId, 1L, "************4444", "Damian Majkowski", EXPIRATION_DATE, true
        );
        when(paymentCardRepository.findById(paymentCardId)).thenReturn(Optional.of(paymentCardEntity));
        when(paymentCardMapper.toDto(paymentCardEntity)).thenReturn(expectedResponse);

        //when
        ResponsePaymentCardDto result = cardService.activatePaymentCard(paymentCardId);

        //then
        assertSame(expectedResponse, result);
        assertTrue(paymentCardEntity.isActive());
        verify(paymentCardRepository).findById(paymentCardId);
        verify(paymentCardMapper).toDto(paymentCardEntity);
    }

    @Test
    void shouldNotActivatePaymentCardWhenPaymentCardIdIsNull() {
        //given
        Long paymentCardId = null;

        //when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> cardService.activatePaymentCard(paymentCardId)
        );

        //then
        assertEquals(ErrorCode.ID_REQUIRED.formatMessage(), exception.getMessage());
        verifyNoInteractions(paymentCardRepository, paymentCardMapper, userRepository);
    }

    @Test
    void shouldNotActivatePaymentCardWhenPaymentCardDoesNotExist() {
        //given
        Long paymentCardId = 2L;
        when(paymentCardRepository.findById(paymentCardId)).thenReturn(Optional.empty());

        //when
        PaymentCardNotFoundByIdException exception = assertThrows(
                PaymentCardNotFoundByIdException.class, () -> cardService.activatePaymentCard(paymentCardId)
        );

        //then
        assertEquals(ErrorCode.PAYMENT_CARD_NOT_FOUND.formatMessage(paymentCardId), exception.getMessage());
        verifyNoInteractions(paymentCardMapper, userRepository);
    }

    @Test
    void shouldNotActivatePaymentCardWhenPaymentCardIsAlreadyActive() {
        //given
        Long paymentCardId = 2L;
        PaymentCardEntity paymentCardEntity = new PaymentCardEntity();
        paymentCardEntity.setId(paymentCardId);
        paymentCardEntity.setActive(true);
        when(paymentCardRepository.findById(paymentCardId)).thenReturn(Optional.of(paymentCardEntity));

        //when
        PaymentCardAlreadyActiveException exception = assertThrows(
                PaymentCardAlreadyActiveException.class, () -> cardService.activatePaymentCard(paymentCardId)
        );

        //then
        assertEquals(ErrorCode.PAYMENT_CARD_ALREADY_ACTIVE.formatMessage(), exception.getMessage());
        assertTrue(paymentCardEntity.isActive());
        verifyNoInteractions(paymentCardMapper, userRepository);
    }

    @Test
    void shouldDeactivatePaymentCard() {
        //given
        Long paymentCardId = 2L;
        PaymentCardEntity paymentCardEntity = new PaymentCardEntity();
        paymentCardEntity.setId(paymentCardId);
        paymentCardEntity.setNumber("1111222233334444");
        paymentCardEntity.setHolder("Damian Majkowski");
        paymentCardEntity.setExpirationDate(EXPIRATION_DATE);
        paymentCardEntity.setActive(true);
        ResponsePaymentCardDto expectedResponse = new ResponsePaymentCardDto(
                paymentCardId, 1L, "************4444", "Damian Majkowski", EXPIRATION_DATE, false
        );
        when(paymentCardRepository.findById(paymentCardId)).thenReturn(Optional.of(paymentCardEntity));
        when(paymentCardMapper.toDto(paymentCardEntity)).thenReturn(expectedResponse);

        //when
        ResponsePaymentCardDto result = cardService.deactivatePaymentCard(paymentCardId);

        //then
        assertSame(expectedResponse, result);
        assertFalse(paymentCardEntity.isActive());
        verify(paymentCardRepository).findById(paymentCardId);
        verify(paymentCardMapper).toDto(paymentCardEntity);
    }

    @Test
    void shouldNotDeactivatePaymentCardWhenPaymentCardIdIsNull() {
        //given
        Long paymentCardId = null;

        //when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> cardService.deactivatePaymentCard(paymentCardId)
        );

        //then
        assertEquals(ErrorCode.ID_REQUIRED.formatMessage(), exception.getMessage());
        verifyNoInteractions(paymentCardRepository, paymentCardMapper, userRepository);
    }

    @Test
    void shouldNotDeactivatePaymentCardWhenPaymentCardDoesNotExist() {
        //given
        Long paymentCardId = 2L;
        when(paymentCardRepository.findById(paymentCardId)).thenReturn(Optional.empty());

        //when
        PaymentCardNotFoundByIdException exception = assertThrows(
                PaymentCardNotFoundByIdException.class, () -> cardService.deactivatePaymentCard(paymentCardId)
        );

        //then
        assertEquals(ErrorCode.PAYMENT_CARD_NOT_FOUND.formatMessage(paymentCardId), exception.getMessage());
        verifyNoInteractions(paymentCardMapper, userRepository);
    }

    @Test
    void shouldNotDeactivatePaymentCardWhenPaymentCardIsAlreadyDeactivated() {
        //given
        Long paymentCardId = 2L;
        PaymentCardEntity paymentCardEntity = new PaymentCardEntity();
        paymentCardEntity.setId(paymentCardId);
        paymentCardEntity.setActive(false);
        when(paymentCardRepository.findById(paymentCardId)).thenReturn(Optional.of(paymentCardEntity));

        //when
        PaymentCardAlreadyDeactivatedException exception = assertThrows(
                PaymentCardAlreadyDeactivatedException.class, () -> cardService.deactivatePaymentCard(paymentCardId)
        );

        //then
        assertEquals(ErrorCode.PAYMENT_CARD_ALREADY_DEACTIVATED.formatMessage(), exception.getMessage());
        assertFalse(paymentCardEntity.isActive());
        verifyNoInteractions(paymentCardMapper, userRepository);
    }
}

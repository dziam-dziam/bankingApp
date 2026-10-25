package innowise.bankingApp.services;

import innowise.bankingApp.exceptions.TooManyPaymentCardsException;
import innowise.bankingApp.exceptions.UserNotFoundByIdException;
import innowise.bankingApp.mappers.PaymentCardMapper;
import innowise.bankingApp.mappers.UserMapper;
import innowise.bankingApp.model.dtos.CreateDtos.CreatePaymentCardDto;
import innowise.bankingApp.model.dtos.CreateDtos.CreateUserDto;
import innowise.bankingApp.model.dtos.ResponseDtos.ResponsePaymentCardDto;
import innowise.bankingApp.model.dtos.ResponseDtos.ResponseUserDto;
import innowise.bankingApp.model.entities.PaymentCard;
import innowise.bankingApp.model.entities.User;
import innowise.bankingApp.repositories.PaymentCardRepository;
import innowise.bankingApp.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateServiceTest {

    private static final LocalDate BIRTH_DATE = LocalDate.of(2002, 9, 27);
    private static final LocalDate EXPIRATION_DATE = LocalDate.of(2030, 12, 12);

    @Mock
    private UserMapper userMapper;

    @Mock
    private PaymentCardMapper paymentCardMapper;

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateService createService;

    @Test
    void shouldCreateNewUser() {
        //given
        CreateUserDto dto = new CreateUserDto(
                "Jan",
                "Kowalski",
                BIRTH_DATE,
                "janek3@gmail.com"
        );
        User user = new User();
        ResponseUserDto expectedResponse = new ResponseUserDto(
                1L,
                "Jan",
                "Kowalski",
                BIRTH_DATE,
                "janek3@gmail.com",
                true
        );

        when(userMapper.toEntity(dto)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedResponse);
        //when
        ResponseUserDto result = createService.createNewUser(dto);
        //then
        assertEquals(expectedResponse, result);
        assertTrue(user.isActive());
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
        verifyNoInteractions(paymentCardRepository, paymentCardMapper);

    }

    @Test
    void shouldRejectNullUserDtoWithoutCallingDependencies() {
        // when / then
        assertThrows(IllegalArgumentException.class,
                () -> createService.createNewUser(null));

        verifyNoInteractions(userRepository, paymentCardRepository,
                userMapper, paymentCardMapper);
    }

    @Test
    void shouldCreateNewPaymentCard() {
        // given
        CreatePaymentCardDto createDto = new CreatePaymentCardDto(
                1L,
                "123456",
                "Jan Kowalski",
                EXPIRATION_DATE
        );

        PaymentCard paymentCard = new PaymentCard();
        User user = new User();

        ResponsePaymentCardDto expectedResponse = new ResponsePaymentCardDto(
                1L,
                1L,
                "123456",
                "Jan Kowalski",
                EXPIRATION_DATE,
                true
        );

        when(paymentCardMapper.toEntity(createDto)).thenReturn(paymentCard);
        when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));
        when(paymentCardMapper.toDto(paymentCard)).thenReturn(expectedResponse);
        when(paymentCardRepository.save(paymentCard)).thenReturn(paymentCard);

        // when
        ResponsePaymentCardDto result =
                createService.createNewPaymentCard(createDto);

        // then
        assertEquals(expectedResponse, result);
        assertTrue(paymentCard.isActive());
        assertSame(user, paymentCard.getUser());

        verify(paymentCardRepository).save(paymentCard);
        verify(paymentCardMapper).toEntity(createDto);
        verify(paymentCardMapper).toDto(paymentCard);
        verify(userRepository).findByIdForUpdate(1L);
    }


    @Test
    void shouldRejectNullPaymentCardDto() {
        // when / then
        assertThrows(IllegalArgumentException.class, () -> createService.createNewPaymentCard(null)
        );

        verifyNoInteractions(
                userRepository,
                paymentCardRepository,
                userMapper,
                paymentCardMapper
        );
    }

    @Test
    void shouldRejectNullUserId() {
        // given
        CreatePaymentCardDto dto = new CreatePaymentCardDto(
                null,
                "123456",
                "Jan Kowalski",
                EXPIRATION_DATE
        );

        // when / then
        assertThrows(IllegalArgumentException.class, () -> createService.createNewPaymentCard(dto)
        );

        verifyNoInteractions(
                userRepository,
                paymentCardRepository,
                userMapper,
                paymentCardMapper
        );
    }

    @Test
    void shouldRejectPaymentCardForNonExistingUser() {
        // given
        CreatePaymentCardDto dto = new CreatePaymentCardDto(
                999L,
                "123456",
                "Jan Kowalski",
                EXPIRATION_DATE
        );

        when(userRepository.findByIdForUpdate(999L)).thenReturn(Optional.empty());

        // when / then
        assertThrows(UserNotFoundByIdException.class, () -> createService.createNewPaymentCard(dto)
        );

        verify(userRepository).findByIdForUpdate(999L);
        verifyNoInteractions(
                paymentCardRepository,
                paymentCardMapper
        );
    }

    @Test
    void shouldRejectSixthPaymentCard() {
        // given
        User user = new User();

        for (int i = 0; i < 5; i++) {
            user.addCard(new PaymentCard());
        }

        PaymentCard newCard = new PaymentCard();

        CreatePaymentCardDto dto = new CreatePaymentCardDto(
                1L,
                "123456",
                "Jan Kowalski",
                EXPIRATION_DATE
        );

        when(userRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(user));

        when(paymentCardMapper.toEntity(dto))
                .thenReturn(newCard);

        // when / then
        assertThrows(TooManyPaymentCardsException.class, () -> createService.createNewPaymentCard(dto)
        );

        assertEquals(5, user.getPaymentCards().size());
        assertNull(newCard.getUser());

        verify(userRepository).findByIdForUpdate(1L);
        verify(paymentCardMapper).toEntity(dto);

        verify(paymentCardRepository, never())
                .save(any(PaymentCard.class));

        verify(paymentCardMapper, never())
                .toDto(any(PaymentCard.class));
    }

}
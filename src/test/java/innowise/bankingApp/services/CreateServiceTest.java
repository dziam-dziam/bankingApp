package innowise.bankingApp.services;

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
        //given
        CreatePaymentCardDto createDto = new CreatePaymentCardDto(
                1L, "123456", "Jan Kowalski", EXPIRATION_DATE
        );
        PaymentCard paymentCard = new PaymentCard();
        ResponsePaymentCardDto expectedResponse = new ResponsePaymentCardDto(
                1L,
                1L,
                "123456",
                "Jan Kowalski",
                EXPIRATION_DATE,
                true
        );
        User user = new User();
        when(paymentCardMapper.toEntity(createDto)).thenReturn(paymentCard);
        when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));
        //when

        ResponsePaymentCardDto result = createService.createNewPaymentCard(createDto);

        //then
        assertEquals(expectedResponse, result);
        assertTrue(paymentCard.isActive());
        verify(paymentCardRepository).save(paymentCard);
        verify(paymentCardMapper).toDto(paymentCard);
        verify(paymentCardMapper).toEntity(createDto);


    }
}
package innowise.bankingApp.services;

import innowise.bankingApp.mappers.PaymentCardMapper;
import innowise.bankingApp.mappers.UserMapper;
import innowise.bankingApp.model.dtos.CreateDtos.CreateUserDto;
import innowise.bankingApp.model.dtos.ResponseDtos.ResponseUserDto;
import innowise.bankingApp.model.entities.User;
import innowise.bankingApp.repositories.PaymentCardRepository;
import innowise.bankingApp.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateServiceTest {

    private static final LocalDate BIRTH_DATE = LocalDate.of(2002,9,27);
    private static final LocalDate EXPIRATION_DATE = LocalDate.of(2030, 12,12);

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
    void should_create_new_user(){
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





}
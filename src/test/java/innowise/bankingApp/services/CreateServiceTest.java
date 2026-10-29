package innowise.bankingApp.services;

import innowise.bankingApp.exceptions.ErrorCode;
import innowise.bankingApp.mappers.UserMapper;
import innowise.bankingApp.model.dto.create.CreateUserDto;
import innowise.bankingApp.model.dto.response.ResponseUserDto;
import innowise.bankingApp.model.entity.User;
import innowise.bankingApp.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final LocalDate BIRTH_DATE = LocalDate.of(2002, 9, 27);

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateNewUser() {
        // given
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

        // when
        ResponseUserDto result = userService.createNewUser(dto);

        // then
        assertEquals(expectedResponse, result);
        assertTrue(user.isActive());

        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    void shouldRejectNullUserDtoWithoutCallingDependencies() {
        // when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createNewUser(null)
        );

        // then
        assertEquals(
                ErrorCode.DTO_REQUIRED.formatMessage(),
                exception.getMessage()
        );

        verifyNoInteractions(userRepository, userMapper);
    }
}
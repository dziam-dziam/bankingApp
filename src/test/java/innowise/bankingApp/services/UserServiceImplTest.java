package innowise.bankingApp.services;

import innowise.bankingApp.exceptions.ErrorCode;
import innowise.bankingApp.exceptions.UserAlreadyActiveException;
import innowise.bankingApp.exceptions.UserAlreadyDeactivatedException;
import innowise.bankingApp.exceptions.UserNotFoundByIdException;
import innowise.bankingApp.mappers.PaymentCardMapper;
import innowise.bankingApp.mappers.UserMapper;
import innowise.bankingApp.model.dto.request.CreateUserDto;
import innowise.bankingApp.model.dto.request.UpdateUserDto;
import innowise.bankingApp.model.dto.response.ResponsePaymentCardDto;
import innowise.bankingApp.model.dto.response.ResponseUserDto;
import innowise.bankingApp.model.dto.response.ResponseUserWithCardsDto;
import innowise.bankingApp.model.entity.PaymentCardEntity;
import innowise.bankingApp.model.entity.UserEntity;
import innowise.bankingApp.repositories.UserRepository;
import innowise.bankingApp.services.impl.UserServiceImpl;
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
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PaymentCardMapper paymentCardMapper;
    @InjectMocks
    private UserServiceImpl userService;

    private final LocalDate BIRTH_DATE = LocalDate.of(2002, 9, 27);

    @Test
    void shouldCreateNewUser() {
        //given
        CreateUserDto createUserDto = new CreateUserDto(
                "Damian", "Majkowski", BIRTH_DATE, "damian2002@gmail.com"
        );
        UserEntity userEntity = new UserEntity();
        userEntity.setName(createUserDto.name());
        userEntity.setSurname(createUserDto.surname());
        userEntity.setBirthDate(createUserDto.birthDate());
        userEntity.setEmail(createUserDto.email());
        userEntity.setActive(false);
        ResponseUserDto expectedResponse = new ResponseUserDto(
                1L, "Damian", "Majkowski", BIRTH_DATE, "damian2002@gmail.com", true
        );
        when(userMapper.toEntity(createUserDto)).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(expectedResponse);

        //when
        ResponseUserDto result = userService.createNewUser(createUserDto);

        //then
        assertSame(expectedResponse, result);
        assertTrue(userEntity.isActive());
        verify(userMapper).toEntity(createUserDto);
        verify(userRepository).save(userEntity);
        verify(userMapper).toDto(userEntity);
    }

    @Test
    void shouldNotCreateNewUserWhenCreateUserDtoIsNull() {
        //given
        CreateUserDto createUserDto = null;

        //when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> userService.createNewUser(createUserDto)
        );

        //then
        assertEquals(ErrorCode.DTO_REQUIRED.formatMessage(), exception.getMessage());
        verifyNoInteractions(userMapper, userRepository, paymentCardMapper);
    }

    @Test
    void shouldGetUser() {
        //given
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setName("Damian");
        userEntity.setSurname("Majkowski");
        userEntity.setBirthDate(BIRTH_DATE);
        userEntity.setEmail("damian2002@gmail.com");
        userEntity.setActive(true);
        ResponseUserDto expectedResponse = new ResponseUserDto(
                userId, "Damian", "Majkowski", BIRTH_DATE, "damian2002@gmail.com", true
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(expectedResponse);

        //when
        ResponseUserDto result = userService.getUser(userId);

        //then
        assertSame(expectedResponse, result);
        verify(userRepository).findById(userId);
        verify(userMapper).toDto(userEntity);
    }

    @Test
    void shouldNotGetUserWhenUserIdIsNull() {
        //given
        Long userId = null;

        //when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> userService.getUser(userId)
        );

        //then
        assertEquals(ErrorCode.ID_REQUIRED.formatMessage(), exception.getMessage());
        verifyNoInteractions(userRepository, userMapper, paymentCardMapper);
    }

    @Test
    void shouldNotGetUserWhenUserDoesNotExist() {
        //given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //when
        UserNotFoundByIdException exception = assertThrows(
                UserNotFoundByIdException.class, () -> userService.getUser(userId)
        );

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND.formatMessage(userId), exception.getMessage());
        verify(userRepository).findById(userId);
        verifyNoInteractions(userMapper, paymentCardMapper);
    }

    @Test
    void shouldGetAllUsers() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        UserEntity userEntity = new UserEntity();
        ResponseUserDto expectedResponse = new ResponseUserDto(
                1L, "Damian", "Majkowski", BIRTH_DATE, "damian2002@gmail.com", true
        );
        Page<UserEntity> users = new PageImpl<>(List.of(userEntity), pageable, 1);
        when(userRepository.findAll(org.mockito.ArgumentMatchers.<Specification<UserEntity>>any(), eq(pageable)))
                .thenReturn(users);
        when(userMapper.toDto(userEntity)).thenReturn(expectedResponse);

        //when
        Page<ResponseUserDto> result = userService.getAllUsers(null, null, pageable);

        //then
        assertEquals(List.of(expectedResponse), result.getContent());
        assertEquals(1L, result.getTotalElements());
        assertEquals(pageable, result.getPageable());
        verify(userMapper).toDto(userEntity);
    }

    @Test
    void shouldGetAllUsersWithNameAndSurname() {
        //given
        String name = "Damian";
        String surname = "Majkowski";
        Pageable pageable = PageRequest.of(1, 2);
        UserEntity userEntity = new UserEntity();
        ResponseUserDto expectedResponse = new ResponseUserDto(
                1L, name, surname, BIRTH_DATE, "damian2002@gmail.com", true
        );
        Page<UserEntity> users = new PageImpl<>(List.of(userEntity), pageable, 3);
        when(userRepository.findAll(org.mockito.ArgumentMatchers.<Specification<UserEntity>>any(), eq(pageable)))
                .thenReturn(users);
        when(userMapper.toDto(userEntity)).thenReturn(expectedResponse);

        //when
        Page<ResponseUserDto> result = userService.getAllUsers(name, surname, pageable);

        //then
        assertEquals(List.of(expectedResponse), result.getContent());
        assertEquals(1, result.getNumber());
        assertEquals(2, result.getSize());
        assertEquals(3L, result.getTotalElements());
        verify(userMapper).toDto(userEntity);
    }

    @Test
    void shouldNotReturnUsersWhenPageIsEmpty() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findAll(org.mockito.ArgumentMatchers.<Specification<UserEntity>>any(), eq(pageable)))
                .thenReturn(Page.empty(pageable));

        //when
        Page<ResponseUserDto> result = userService.getAllUsers(null, null, pageable);

        //then
        assertTrue(result.isEmpty());
        assertEquals(0L, result.getTotalElements());
        assertEquals(pageable, result.getPageable());
        verifyNoInteractions(userMapper, paymentCardMapper);
    }

    @Test
    void shouldUpdateUser() {
        //given
        Long userId = 1L;
        UserEntity notUpdatedUserEntity = new UserEntity();
        notUpdatedUserEntity.setId(userId);
        notUpdatedUserEntity.setName("Damian");
        notUpdatedUserEntity.setSurname("Majkowski");
        notUpdatedUserEntity.setBirthDate(BIRTH_DATE);
        notUpdatedUserEntity.setEmail("damian2002@gmail.com");
        notUpdatedUserEntity.setActive(true);
        UpdateUserDto updateUserDto = new UpdateUserDto(
                userId, "Maciek", "Markowiak", LocalDate.of(1998, 12, 12), "maciek123@gmail.com"
        );
        UserEntity updatedUserEntity = new UserEntity();
        updatedUserEntity.setId(userId);
        updatedUserEntity.setName(updateUserDto.name());
        updatedUserEntity.setSurname(updateUserDto.surname());
        updatedUserEntity.setBirthDate(updateUserDto.birthDate());
        updatedUserEntity.setEmail(updateUserDto.email());
        updatedUserEntity.setActive(true);
        UserEntity savedUserEntity = new UserEntity();
        savedUserEntity.setId(userId);
        savedUserEntity.setName(updateUserDto.name());
        savedUserEntity.setSurname(updateUserDto.surname());
        savedUserEntity.setBirthDate(updateUserDto.birthDate());
        savedUserEntity.setEmail(updateUserDto.email());
        savedUserEntity.setActive(true);
        ResponseUserDto expectedResponse = new ResponseUserDto(
                userId, "Maciek", "Markowiak", LocalDate.of(1998, 12, 12), "maciek123@gmail.com", true
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(notUpdatedUserEntity));
        when(userMapper.updateEntity(updateUserDto, notUpdatedUserEntity)).thenReturn(updatedUserEntity);
        when(userRepository.save(updatedUserEntity)).thenReturn(savedUserEntity);
        when(userMapper.toDto(savedUserEntity)).thenReturn(expectedResponse);

        //when
        ResponseUserDto result = userService.updateUser(userId, updateUserDto);

        //then
        assertSame(expectedResponse, result);
        verify(userRepository).findById(userId);
        verify(userMapper).updateEntity(updateUserDto, notUpdatedUserEntity);
        verify(userRepository).save(updatedUserEntity);
        verify(userMapper).toDto(savedUserEntity);
    }

    @Test
    void shouldNotUpdateUserWhenUserIdIsNull() {
        //given
        Long userId = null;
        UpdateUserDto updateUserDto = new UpdateUserDto(
                1L, "Maciek", "Markowiak", BIRTH_DATE, "maciek123@gmail.com"
        );

        //when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> userService.updateUser(userId, updateUserDto)
        );

        //then
        assertEquals(ErrorCode.ID_REQUIRED.formatMessage(), exception.getMessage());
        verifyNoInteractions(userRepository, userMapper, paymentCardMapper);
    }

    @Test
    void shouldNotUpdateUserWhenUpdateUserDtoIsNull() {
        //given
        Long userId = 1L;
        UpdateUserDto updateUserDto = null;

        //when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> userService.updateUser(userId, updateUserDto)
        );

        //then
        assertEquals(ErrorCode.UPDATE_DTO_REQUIRED.formatMessage(), exception.getMessage());
        verifyNoInteractions(userRepository, userMapper, paymentCardMapper);
    }

    @Test
    void shouldNotUpdateUserWhenUserDoesNotExist() {
        //given
        Long userId = 1L;
        UpdateUserDto updateUserDto = new UpdateUserDto(
                userId, "Maciek", "Markowiak", BIRTH_DATE, "maciek123@gmail.com"
        );
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //when
        UserNotFoundByIdException exception = assertThrows(
                UserNotFoundByIdException.class, () -> userService.updateUser(userId, updateUserDto)
        );

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND.formatMessage(userId), exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
        verifyNoInteractions(userMapper, paymentCardMapper);
    }

    @Test
    void shouldDeleteUser() {
        //given
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        //when
        userService.deleteUser(userId);

        //then
        verify(userRepository).findById(userId);
        verify(userRepository).delete(userEntity);
        verifyNoInteractions(userMapper, paymentCardMapper);
    }

    @Test
    void shouldNotDeleteUserWhenUserDoesNotExist() {
        //given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //when
        UserNotFoundByIdException exception = assertThrows(
                UserNotFoundByIdException.class, () -> userService.deleteUser(userId)
        );

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND.formatMessage(userId), exception.getMessage());
        verify(userRepository, never()).delete(any(UserEntity.class));
        verifyNoInteractions(userMapper, paymentCardMapper);
    }

    @Test
    void shouldDeleteAllUsers() {
        //given

        //when
        userService.deleteAllUsers();

        //then
        verify(userRepository).deleteAll();
        verifyNoInteractions(userMapper, paymentCardMapper);
    }

    @Test
    void shouldActivateUser() {
        //given
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setActive(false);
        ResponseUserDto expectedResponse = new ResponseUserDto(
                userId, "Damian", "Majkowski", BIRTH_DATE, "damian2002@gmail.com", true
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(expectedResponse);

        //when
        ResponseUserDto result = userService.activateUser(userId);

        //then
        assertSame(expectedResponse, result);
        assertTrue(userEntity.isActive());
        verify(userRepository).findById(userId);
        verify(userMapper).toDto(userEntity);
    }

    @Test
    void shouldNotActivateUserWhenUserIdIsNull() {
        //given
        Long userId = null;

        //when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> userService.activateUser(userId)
        );

        //then
        assertEquals(ErrorCode.ID_REQUIRED.formatMessage(), exception.getMessage());
        verifyNoInteractions(userRepository, userMapper, paymentCardMapper);
    }

    @Test
    void shouldNotActivateUserWhenUserDoesNotExist() {
        //given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //when
        UserNotFoundByIdException exception = assertThrows(
                UserNotFoundByIdException.class, () -> userService.activateUser(userId)
        );

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND.formatMessage(userId), exception.getMessage());
        verifyNoInteractions(userMapper, paymentCardMapper);
    }

    @Test
    void shouldNotActivateUserWhenUserIsAlreadyActive() {
        //given
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setActive(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        //when
        UserAlreadyActiveException exception = assertThrows(
                UserAlreadyActiveException.class, () -> userService.activateUser(userId)
        );

        //then
        assertEquals(ErrorCode.USER_ALREADY_ACTIVE.formatMessage(), exception.getMessage());
        assertTrue(userEntity.isActive());
        verifyNoInteractions(userMapper, paymentCardMapper);
    }

    @Test
    void shouldDeactivateUser() {
        //given
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setActive(true);
        ResponseUserDto expectedResponse = new ResponseUserDto(
                userId, "Damian", "Majkowski", BIRTH_DATE, "damian2002@gmail.com", false
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(expectedResponse);

        //when
        ResponseUserDto result = userService.deactivateUser(userId);

        //then
        assertSame(expectedResponse, result);
        assertFalse(userEntity.isActive());
        verify(userRepository).findById(userId);
        verify(userMapper).toDto(userEntity);
    }

    @Test
    void shouldNotDeactivateUserWhenUserIdIsNull() {
        //given
        Long userId = null;

        //when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> userService.deactivateUser(userId)
        );

        //then
        assertEquals(ErrorCode.ID_REQUIRED.formatMessage(), exception.getMessage());
        verifyNoInteractions(userRepository, userMapper, paymentCardMapper);
    }

    @Test
    void shouldNotDeactivateUserWhenUserDoesNotExist() {
        //given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //when
        UserNotFoundByIdException exception = assertThrows(
                UserNotFoundByIdException.class, () -> userService.deactivateUser(userId)
        );

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND.formatMessage(userId), exception.getMessage());
        verifyNoInteractions(userMapper, paymentCardMapper);
    }

    @Test
    void shouldNotDeactivateUserWhenUserIsAlreadyDeactivated() {
        //given
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setActive(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        //when
        UserAlreadyDeactivatedException exception = assertThrows(
                UserAlreadyDeactivatedException.class, () -> userService.deactivateUser(userId)
        );

        //then
        assertEquals(ErrorCode.USER_ALREADY_DEACTIVATED.formatMessage(), exception.getMessage());
        assertFalse(userEntity.isActive());
        verifyNoInteractions(userMapper, paymentCardMapper);
    }

    @Test
    void shouldGetUserWithCards() {
        //given
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        PaymentCardEntity paymentCardEntity = new PaymentCardEntity();
        paymentCardEntity.setId(2L);
        paymentCardEntity.setNumber("1111222233334444");
        PaymentCardEntity secondPaymentCardEntity = new PaymentCardEntity();
        secondPaymentCardEntity.setId(3L);
        secondPaymentCardEntity.setNumber("5555666677778888");
        userEntity.addCard(paymentCardEntity);
        userEntity.addCard(secondPaymentCardEntity);
        ResponseUserDto expectedResponse = new ResponseUserDto(
                userId, "Damian", "Majkowski", BIRTH_DATE, "damian2002@gmail.com", true
        );
        ResponsePaymentCardDto expectedCardResponse = new ResponsePaymentCardDto(
                2L, userId, "1111222233334444", "Damian Majkowski", LocalDate.of(2030, 12, 31), true
        );
        ResponsePaymentCardDto expectedSecondCardResponse = new ResponsePaymentCardDto(
                3L, userId, "5555666677778888", "Damian Majkowski", LocalDate.of(2030, 12, 31), true
        );
        when(userRepository.findWithCardsById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(expectedResponse);
        when(paymentCardMapper.toDto(paymentCardEntity)).thenReturn(expectedCardResponse);
        when(paymentCardMapper.toDto(secondPaymentCardEntity)).thenReturn(expectedSecondCardResponse);

        //when
        ResponseUserWithCardsDto result = userService.getUserWithCards(userId);

        //then
        assertSame(expectedResponse, result.user());
        assertEquals(List.of(expectedCardResponse, expectedSecondCardResponse), result.cards());
        verify(userRepository).findWithCardsById(userId);
        verify(userMapper).toDto(userEntity);
        verify(paymentCardMapper).toDto(paymentCardEntity);
        verify(paymentCardMapper).toDto(secondPaymentCardEntity);
    }

    @Test
    void shouldGetUserWithCardsWhenUserHasNoCards() {
        //given
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        ResponseUserDto expectedResponse = new ResponseUserDto(
                userId, "Damian", "Majkowski", BIRTH_DATE, "damian2002@gmail.com", true
        );
        when(userRepository.findWithCardsById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(expectedResponse);

        //when
        ResponseUserWithCardsDto result = userService.getUserWithCards(userId);

        //then
        assertSame(expectedResponse, result.user());
        assertTrue(result.cards().isEmpty());
        verify(userRepository).findWithCardsById(userId);
        verify(userMapper).toDto(userEntity);
        verifyNoInteractions(paymentCardMapper);
    }

    @Test
    void shouldNotGetUserWithCardsWhenUserIdIsNull() {
        //given
        Long userId = null;

        //when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> userService.getUserWithCards(userId)
        );

        //then
        assertEquals(ErrorCode.ID_REQUIRED.formatMessage(), exception.getMessage());
        verifyNoInteractions(userRepository, userMapper, paymentCardMapper);
    }

    @Test
    void shouldNotGetUserWithCardsWhenUserDoesNotExist() {
        //given
        Long userId = 1L;
        when(userRepository.findWithCardsById(userId)).thenReturn(Optional.empty());

        //when
        UserNotFoundByIdException exception = assertThrows(
                UserNotFoundByIdException.class, () -> userService.getUserWithCards(userId)
        );

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND.formatMessage(userId), exception.getMessage());
        verify(userRepository).findWithCardsById(userId);
        verifyNoInteractions(userMapper, paymentCardMapper);
    }
}

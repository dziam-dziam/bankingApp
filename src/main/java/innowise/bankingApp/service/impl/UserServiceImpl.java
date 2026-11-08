package innowise.bankingApp.service.impl;

import innowise.bankingApp.exceptions.ErrorCode;
import innowise.bankingApp.exceptions.UserAlreadyActiveException;
import innowise.bankingApp.exceptions.UserAlreadyDeactivatedException;
import innowise.bankingApp.exceptions.UserNotFoundByIdException;
import innowise.bankingApp.mappers.PaymentCardMapper;
import innowise.bankingApp.mappers.UserMapper;
import innowise.bankingApp.model.dto.request.CreateUserDto;
import innowise.bankingApp.model.dto.request.UpdateUserDto;
import innowise.bankingApp.model.dto.response.ResponseUserDto;
import innowise.bankingApp.model.dto.response.ResponseUserWithCardsDto;
import innowise.bankingApp.model.entity.UserEntity;
import innowise.bankingApp.repository.UserRepository;
import innowise.bankingApp.service.UserService;
import innowise.bankingApp.specifications.UserSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PaymentCardMapper paymentCardMapper;

    public ResponseUserDto createNewUser(CreateUserDto dto) {
        validateIfNotNull(dto, ErrorCode.DTO_REQUIRED);

        return createNewUserAndReturnResponseDto(dto);
    }


    public ResponseUserDto getUser(Long userId) {
        validateIfNotNull(userId, ErrorCode.ID_REQUIRED);

        return getUserAndReturnResponseDto(userId);
    }

    @Transactional(readOnly = true)
    public Page<ResponseUserDto> getAllUsers(String name, String surname, Pageable pageable) {
        return userRepository.findAll(UserSpecifications.filter(name, surname), pageable).map(userMapper::toDto);
    }

    @Transactional
    @CacheEvict(cacheNames = "usersWithCards", key = "#userId")
    public ResponseUserDto updateUser(Long userId, UpdateUserDto updateUserDto) {
        validateArguments(userId, updateUserDto);

        return updateUserAndReturnResponseDto(userId, updateUserDto);
    }

    @Transactional
    @CacheEvict(cacheNames = "usersWithCards", key = "#userId")
    public void deleteUser(Long userId) {
        findAndDeleteUser(userId);
    }

    @Transactional
    @CacheEvict(cacheNames = "usersWithCards", allEntries = true)
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    @Transactional
    @CacheEvict(cacheNames = "usersWithCards", key = "#userId")
    public ResponseUserDto activateUser(Long userId) {
        validateIfNotNull(userId, ErrorCode.ID_REQUIRED);

        UserEntity user = findUserAndCheckIfActive(userId);
        user.setActive(true);

        return userMapper.toDto(user);
    }

    @Transactional
    @CacheEvict(cacheNames = "usersWithCards", key = "#userId")
    public ResponseUserDto deactivateUser(Long userId) {
        validateIfNotNull(userId, ErrorCode.ID_REQUIRED);

        UserEntity user = findUserAndCheckIfDeactivated(userId);
        user.setActive(false);

        return userMapper.toDto(user);
    }


    @Cacheable(cacheNames = "usersWithCards", key = "#userId", condition = "#userId != null")
    @Transactional(readOnly = true)
    public ResponseUserWithCardsDto getUserWithCards(Long userId) {
        validateIfNotNull(userId, ErrorCode.ID_REQUIRED);

        UserEntity user = userRepository.findWithCardsById(userId).orElseThrow(() -> new UserNotFoundByIdException(userId));

        return new ResponseUserWithCardsDto(userMapper.toDto(user), user.getPaymentCards()
                .stream().map(paymentCardMapper::toDto).toList());
    }

    private ResponseUserDto createNewUserAndReturnResponseDto(CreateUserDto dto) {
        UserEntity newUser = userMapper.toEntity(dto);
        newUser.setActive(true);
        userRepository.save(newUser);

        return userMapper.toDto(newUser);
    }

    private ResponseUserDto getUserAndReturnResponseDto(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundByIdException(userId));

        return userMapper.toDto(user);
    }

    private static void validateArguments(Long userId, UpdateUserDto updateUserDto) {
        validateIfNotNull(userId, ErrorCode.ID_REQUIRED);
        validateIfNotNull(updateUserDto, ErrorCode.UPDATE_DTO_REQUIRED);
    }

    private ResponseUserDto updateUserAndReturnResponseDto(Long userId, UpdateUserDto updateUserDto) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundByIdException(userId));

        UserEntity updatedUser = userMapper.updateEntity(updateUserDto, user);

        return userMapper.toDto(updatedUser);
    }

    private void findAndDeleteUser(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundByIdException(userId));

        userRepository.delete(user);
    }

    private UserEntity findUserAndCheckIfActive(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundByIdException(userId));

        if (user.isActive()) {
            throw new UserAlreadyActiveException();
        }

        return user;
    }

    private UserEntity findUserAndCheckIfDeactivated(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundByIdException(userId));

        if (!user.isActive()) {
            throw new UserAlreadyDeactivatedException();
        }

        return user;
    }

    private static void validateIfNotNull(Object value, ErrorCode errorCode) {
        if (value == null) {
            throw new IllegalArgumentException(errorCode.formatMessage());
        }
    }
}

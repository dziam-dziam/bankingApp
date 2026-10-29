package innowise.bankingApp.services;

import innowise.bankingApp.exceptions.ErrorCode;
import innowise.bankingApp.exceptions.UserAlreadyActiveException;
import innowise.bankingApp.exceptions.UserAlreadyDeactivatedException;
import innowise.bankingApp.exceptions.UserNotFoundByIdException;
import innowise.bankingApp.mappers.UserMapper;
import innowise.bankingApp.model.dto.create.CreateUserDto;
import innowise.bankingApp.model.dto.response.ResponseUserDto;
import innowise.bankingApp.model.dto.update.UpdateUserDto;
import innowise.bankingApp.model.entity.User;
import innowise.bankingApp.repositories.UserRepository;
import innowise.bankingApp.specifications.UserSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public ResponseUserDto createNewUser(CreateUserDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException(ErrorCode.DTO_REQUIRED.formatMessage());
        }

        User newUser = userMapper.toEntity(dto);
        newUser.setActive(true);
        userRepository.save(newUser);

        return userMapper.toDto(newUser);
    }

    public ResponseUserDto getUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException(ErrorCode.ID_REQUIRED.formatMessage());
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundByIdException(userId));

        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public Page<ResponseUserDto> getAllUsers(String name, String surname, Pageable pageable) {
        return userRepository.findAll(UserSpecifications.filter(name, surname), pageable).map(userMapper::toDto);
    }

    @Transactional
    public ResponseUserDto updateUser(Long userId, UpdateUserDto updateUserDto) {
        if (userId == null) {
            throw new IllegalArgumentException(ErrorCode.ID_REQUIRED.formatMessage());
        }
        if (updateUserDto == null) {
            throw new IllegalArgumentException(ErrorCode.UPDATE_DTO_REQUIRED.formatMessage());
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundByIdException(userId));

        User updatedUser = userMapper.updateEntity(updateUserDto, user);
        User savedUser = userRepository.save(updatedUser);

        return userMapper.toDto(savedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundByIdException(userId));

        userRepository.delete(user);
    }

    @Transactional
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    @Transactional
    public ResponseUserDto activateUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException(ErrorCode.ID_REQUIRED.formatMessage());
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundByIdException(userId));

        if (user.isActive()) {
            throw new UserAlreadyActiveException();
        }

        user.setActive(true);
        return userMapper.toDto(user);
    }

    @Transactional
    public ResponseUserDto deactivateUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException(ErrorCode.ID_REQUIRED.formatMessage());
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundByIdException(userId));

        if (!user.isActive()) {
            throw new UserAlreadyDeactivatedException();
        }

        user.setActive(false);
        return userMapper.toDto(user);
    }
}
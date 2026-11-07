package innowise.bankingApp.service;

import innowise.bankingApp.model.dto.request.CreateUserDto;
import innowise.bankingApp.model.dto.request.UpdateUserDto;
import innowise.bankingApp.model.dto.response.ResponseUserDto;
import innowise.bankingApp.model.dto.response.ResponseUserWithCardsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    ResponseUserDto createNewUser(CreateUserDto dto);

    ResponseUserDto getUser(Long userId);

    Page<ResponseUserDto> getAllUsers(String name, String surname, Pageable pageable);

    ResponseUserDto updateUser(Long userId, UpdateUserDto updateUserDto);

    void deleteUser(Long userId);

    void deleteAllUsers();

    ResponseUserDto activateUser(Long userId);

    ResponseUserDto deactivateUser(Long userId);

    ResponseUserWithCardsDto getUserWithCards(Long userId);
}

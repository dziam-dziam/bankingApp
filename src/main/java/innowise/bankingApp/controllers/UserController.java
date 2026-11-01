package innowise.bankingApp.controllers;

import innowise.bankingApp.model.dto.request.CreateUserDto;
import innowise.bankingApp.model.dto.request.UpdateUserDto;
import innowise.bankingApp.model.dto.response.ResponseUserDto;
import innowise.bankingApp.model.dto.response.ResponseUserWithCardsDto;
import innowise.bankingApp.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<ResponseUserDto> createNewUser(@Valid @RequestBody CreateUserDto createUserDto) {
        return ResponseEntity.ok(userService.createNewUser(createUserDto));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseUserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/getAll")
    public ResponseEntity<Page<ResponseUserDto>> getAllUsers(@RequestParam(required = false) String name, @RequestParam(required = false) String surname, Pageable pageable) {
        Page<ResponseUserDto> users = userService.getAllUsers(name, surname, pageable);

        return ResponseEntity.ok(users);
    }

    @GetMapping("/getUserWithCards/{id}")
    public ResponseEntity<ResponseUserWithCardsDto> getUserWithCards(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserWithCards(id));
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> deleteAllUsers() {
        userService.deleteAllUsers();

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<ResponseUserDto> updateUser(@PathVariable Long userId, @Valid @RequestBody UpdateUserDto updateUserDto) {
        return ResponseEntity.ok(userService.updateUser(userId, updateUserDto));
    }

    @PutMapping("activate/{userId}")
    public ResponseEntity<ResponseUserDto> activateUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.activateUser(userId));
    }

    @Transactional
    @PutMapping("deactivate/{userId}")
    public ResponseEntity<ResponseUserDto> deactivateUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.deactivateUser(userId));
    }
}

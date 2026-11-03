package innowise.bankingApp.controllers;

import innowise.bankingApp.model.dto.request.CreateUserDto;
import innowise.bankingApp.model.dto.request.UpdateUserDto;
import innowise.bankingApp.model.dto.response.ResponseUserDto;
import innowise.bankingApp.model.dto.response.ResponseUserWithCardsDto;
import innowise.bankingApp.services.interfaces.UserService;
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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> deleteAllUsers() {
        userService.deleteAllUsers();

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseUserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserDto updateUserDto) {
        return ResponseEntity.ok(userService.updateUser(id, updateUserDto));
    }

    @PutMapping("activate/{id}")
    public ResponseEntity<ResponseUserDto> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    @Transactional
    @PutMapping("deactivate/{id}")
    public ResponseEntity<ResponseUserDto> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }
}

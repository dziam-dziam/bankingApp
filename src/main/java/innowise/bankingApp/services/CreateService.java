package innowise.bankingApp.services;

import innowise.bankingApp.model.dtos.CreateDtos.CreatePaymentCardDto;
import innowise.bankingApp.model.dtos.CreateDtos.CreateUserDto;
import innowise.bankingApp.model.dtos.ResponseDtos.ResponsePaymentCardDto;
import innowise.bankingApp.model.dtos.ResponseDtos.ResponseUserDto;
import innowise.bankingApp.model.entities.PaymentCard;
import innowise.bankingApp.model.entities.User;
import innowise.bankingApp.exceptions.UserNotFoundByIdException;
import innowise.bankingApp.mappers.PaymentCardMapper;
import innowise.bankingApp.mappers.UserMapper;
import innowise.bankingApp.repositories.PaymentCardRepository;
import innowise.bankingApp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateService {

    private final UserMapper userMapper;
    private final PaymentCardMapper paymentCardMapper;
    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;

    public ResponseUserDto createNewUser(CreateUserDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dto cannot be null");
        }

        User newUser = userMapper.toEntity(dto);
        newUser.setActive(true);
        userRepository.save(newUser);

        return userMapper.toDto(newUser);
    }

    @Transactional
    public ResponsePaymentCardDto createNewPaymentCard(CreatePaymentCardDto createDto) {
        if (createDto == null) {
            throw new IllegalArgumentException("Dto cannot be null");
        }

        Long userId = createDto.userId();

        if (userId == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }

        User user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new UserNotFoundByIdException(userId));

        PaymentCard newPaymentCard = paymentCardMapper.toEntity(createDto);
        newPaymentCard.setActive(true);
        user.addCard(newPaymentCard);
        PaymentCard savedCard = paymentCardRepository.save(newPaymentCard);

        return paymentCardMapper.toDto(savedCard);
    }
}

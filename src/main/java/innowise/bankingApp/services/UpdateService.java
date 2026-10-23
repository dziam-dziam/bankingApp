package innowise.bankingApp.services;

import innowise.bankingApp.model.dtos.ResponseDtos.ResponsePaymentCardDto;
import innowise.bankingApp.model.dtos.ResponseDtos.ResponseUserDto;
import innowise.bankingApp.model.dtos.UpdateDtos.UpdatePaymentCardDto;
import innowise.bankingApp.model.dtos.UpdateDtos.UpdateUserDto;
import innowise.bankingApp.model.entities.PaymentCard;
import innowise.bankingApp.model.entities.User;
import innowise.bankingApp.exceptions.PaymentCardNotFoundByIdException;
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
public class UpdateService {

    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PaymentCardMapper paymentCardMapper;

    @Transactional
    public ResponseUserDto updateUser(Long userId, UpdateUserDto updateUserDto) {
        if (userId == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (updateUserDto == null) {
            throw new IllegalArgumentException("Update dto cannot be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException(userId));

        user.setName(updateUserDto.name());
        user.setSurname(updateUserDto.surname());
        user.setEmail(updateUserDto.email());
        user.setBirthDate(updateUserDto.birthDate());

        userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Transactional
    public ResponsePaymentCardDto updatePaymentCard(Long cardId, UpdatePaymentCardDto updateDto) {
        if (cardId == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (updateDto == null) {
            throw new IllegalArgumentException("Update dto cannot be null");
        }

        PaymentCard paymentCard = paymentCardRepository.findById(cardId)
                .orElseThrow(() -> new PaymentCardNotFoundByIdException(cardId));

        paymentCard.setHolder(updateDto.holder());
        paymentCard.setExpirationDate(updateDto.expirationDate());

        paymentCardRepository.save(paymentCard);
        return paymentCardMapper.toDto(paymentCard);
    }
}

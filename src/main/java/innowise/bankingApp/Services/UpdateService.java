package innowise.bankingApp.Services;

import innowise.bankingApp.Dtos.ResponseDtos.ResponsePaymentCardDto;
import innowise.bankingApp.Dtos.ResponseDtos.ResponseUserDto;
import innowise.bankingApp.Dtos.UpdateDtos.UpdatePaymentCardDto;
import innowise.bankingApp.Dtos.UpdateDtos.UpdateUserDto;
import innowise.bankingApp.Entities.PaymentCard;
import innowise.bankingApp.Entities.User;
import innowise.bankingApp.Exceptions.PaymentCardNotFoundByIdException;
import innowise.bankingApp.Exceptions.UserNotFoundByIdException;
import innowise.bankingApp.Mappers.PaymentCardMapper;
import innowise.bankingApp.Mappers.UserMapper;
import innowise.bankingApp.Repositories.PaymentCardRepository;
import innowise.bankingApp.Repositories.UserRepository;
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
    public ResponseUserDto updateUser(Long userId, UpdateUserDto updateUserDto){
        if (userId == null){
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (updateUserDto == null){
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
    public ResponsePaymentCardDto updatePaymentCard(Long cardId, UpdatePaymentCardDto updateDto){
        if (cardId == null){
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (updateDto == null){
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

package innowise.bankingApp.Services;

import innowise.bankingApp.Dtos.ResponseDtos.ResponsePaymentCardDto;
import innowise.bankingApp.Dtos.ResponseDtos.ResponseUserDto;
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

@Service
@RequiredArgsConstructor
public class DeActivateService {

    private final PaymentCardMapper paymentCardMapper;
    private final UserMapper userMapper;
    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;

    public ResponsePaymentCardDto activatePaymentCard(Long cardId){
        if (cardId == null){
            throw new IllegalStateException("Id cannot be null");
        }
        PaymentCard paymentCard = paymentCardRepository.findById(cardId)
                .orElseThrow(() -> new PaymentCardNotFoundByIdException(cardId));

        if (paymentCard.isActive()){
            throw new IllegalStateException("Payment card is already active");
        }

        paymentCard.setActive(true);
        paymentCardRepository.save(paymentCard);
        return paymentCardMapper.toDto(paymentCard);
    }
    public ResponsePaymentCardDto deactivatePaymentCard(Long cardId){
        if (cardId == null){
            throw new IllegalStateException("Id cannot be null");
        }
        PaymentCard paymentCard = paymentCardRepository.findById(cardId)
                .orElseThrow(() -> new PaymentCardNotFoundByIdException(cardId));

        if (!paymentCard.isActive()){
            throw new IllegalStateException("Payment card is already deactivated");
        }

        paymentCard.setActive(false);
        paymentCardRepository.save(paymentCard);
        return paymentCardMapper.toDto(paymentCard);
    }

    public ResponseUserDto activateUser(Long userId){
        if (userId == null){
            throw new IllegalStateException("Id cannot be null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException(userId));

        if (user.isActive()){
            throw new IllegalStateException("User is already active");
        }

        user.setActive(true);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public ResponseUserDto deactivateUser(Long userId){
        if (userId == null){
            throw new IllegalStateException("Id cannot be null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException(userId));

        if (!user.isActive()){
            throw new IllegalStateException("User is already deactivated");
        }

        user.setActive(false);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

}

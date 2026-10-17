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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetService{

    private final UserMapper userMapper;
    private final PaymentCardMapper paymentCardMapper;
    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;

    public ResponseUserDto getUser(Long userId){
        if (userId == null){
            throw new IllegalArgumentException("Id cannot be null");
        }

        User user = userRepository.findById(userId).
                orElseThrow(() -> new UserNotFoundByIdException(userId));

        return userMapper.toDto(user);
    }

    public ResponsePaymentCardDto getPaymentCard(Long paymentCardId){
        if (paymentCardId == null ){
            throw new IllegalArgumentException("PaymentCardId cannot be null");
        }

        PaymentCard paymentCard = paymentCardRepository.findById(paymentCardId).
                orElseThrow(() -> new PaymentCardNotFoundByIdException(paymentCardId));

        return paymentCardMapper.toDto(paymentCard);
    }

    public List<ResponseUserDto> getAllUsers(){
        List<ResponseUserDto> returnList = new ArrayList<>();
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers){
            returnList.add(userMapper.toDto(user));
        }
        return returnList;
    }

    public List<ResponsePaymentCardDto> getAllPaymentCards(){
        List<ResponsePaymentCardDto> returnList = new ArrayList<>();
        List<PaymentCard> allPaymentCards = paymentCardRepository.findAll();
        for (PaymentCard paymentCard : allPaymentCards){
            returnList.add(paymentCardMapper.toDto(paymentCard));
        }
        return returnList;
    }
}

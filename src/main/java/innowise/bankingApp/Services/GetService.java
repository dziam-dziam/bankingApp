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
import innowise.bankingApp.Specifications.PaymentCardSpecifications;
import innowise.bankingApp.Specifications.UserSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional(readOnly = true)
    public Page<ResponseUserDto> getAllUsers(String name, String surname, Pageable pageable){
        return userRepository.findAll(
                UserSpecifications.filter(name, surname), pageable).map(userMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ResponsePaymentCardDto> getAllPaymentCards(String name, String surname, Pageable pageable){
        return paymentCardRepository.findAll(
                PaymentCardSpecifications.filter(name, surname), pageable).map(paymentCardMapper::toDto);
    }

    public List<ResponsePaymentCardDto> getAllPaymentCardsByUserId(Long userId){
        if (userId == null){
            throw new IllegalArgumentException("User id cannot be null");
        }
        return paymentCardRepository.getAllPaymentCardsByUserId(userId);
    }
}

package innowise.bankingApp.services;

import innowise.bankingApp.model.dtos.ResponseDtos.ResponsePaymentCardDto;
import innowise.bankingApp.model.dtos.ResponseDtos.ResponseUserDto;
import innowise.bankingApp.model.entities.PaymentCard;
import innowise.bankingApp.model.entities.User;
import innowise.bankingApp.exceptions.PaymentCardNotFoundByIdException;
import innowise.bankingApp.exceptions.UserNotFoundByIdException;
import innowise.bankingApp.mappers.PaymentCardMapper;
import innowise.bankingApp.mappers.UserMapper;
import innowise.bankingApp.repositories.PaymentCardRepository;
import innowise.bankingApp.repositories.UserRepository;
import innowise.bankingApp.specifications.PaymentCardSpecifications;
import innowise.bankingApp.specifications.UserSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetService {

    private final UserMapper userMapper;
    private final PaymentCardMapper paymentCardMapper;
    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;

    public ResponseUserDto getUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        User user = userRepository.findById(userId).
                orElseThrow(() -> new UserNotFoundByIdException(userId));

        return userMapper.toDto(user);
    }

    public ResponsePaymentCardDto getPaymentCard(Long paymentCardId) {
        if (paymentCardId == null) {
            throw new IllegalArgumentException("PaymentCardId cannot be null");
        }

        PaymentCard paymentCard = paymentCardRepository.findById(paymentCardId).
                orElseThrow(() -> new PaymentCardNotFoundByIdException(paymentCardId));

        return paymentCardMapper.toDto(paymentCard);
    }

    @Transactional(readOnly = true)
    public Page<ResponseUserDto> getAllUsers(String name, String surname, Pageable pageable) {
        return userRepository.findAll(
                UserSpecifications.filter(name, surname), pageable).map(userMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ResponsePaymentCardDto> getAllPaymentCards(String name, String surname, Pageable pageable) {
        return paymentCardRepository.findAll(
                PaymentCardSpecifications.filter(name, surname), pageable).map(paymentCardMapper::toDto);
    }

    public List<ResponsePaymentCardDto> getAllPaymentCardsByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }
        return paymentCardRepository.getAllPaymentCardsByUserId(userId);
    }
}

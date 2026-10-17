package innowise.bankingApp.Services;

import innowise.bankingApp.Dtos.CreateDtos.CreatePaymentCardDto;
import innowise.bankingApp.Dtos.CreateDtos.CreateUserDto;
import innowise.bankingApp.Dtos.ResponseDtos.ResponsePaymentCardDto;
import innowise.bankingApp.Dtos.ResponseDtos.ResponseUserDto;
import innowise.bankingApp.Entities.PaymentCard;
import innowise.bankingApp.Entities.User;
import innowise.bankingApp.Mappers.PaymentCardMapper;
import innowise.bankingApp.Mappers.UserMapper;
import innowise.bankingApp.Repositories.PaymentCardRepository;
import innowise.bankingApp.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateService {

    private final UserMapper userMapper;
    private final PaymentCardMapper paymentCardMapper;
    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;

    public ResponseUserDto createNewUser(CreateUserDto dto){
        if (dto == null){
            throw new IllegalArgumentException("Dto cannot be null");
        }

        User newUser = userMapper.toEntity(dto);
        newUser.setActive(true);
        userRepository.save(newUser);

        return userMapper.toDto(newUser);
    }

    public ResponsePaymentCardDto createNewPaymentCard(CreatePaymentCardDto dto){
        if (dto == null){
            throw new IllegalArgumentException("Dto cannot be null");
        }

        PaymentCard newPaymentCard = paymentCardMapper.toEntity(dto);
        newPaymentCard.setActive(true);
        paymentCardRepository.save(newPaymentCard);

        return paymentCardMapper.toDto(newPaymentCard);
    }
}

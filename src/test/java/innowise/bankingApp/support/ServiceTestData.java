package innowise.bankingApp.support;

import innowise.bankingApp.model.dto.request.CreatePaymentCardDto;
import innowise.bankingApp.model.dto.request.CreateUserDto;
import innowise.bankingApp.model.dto.response.ResponsePaymentCardDto;
import innowise.bankingApp.model.dto.response.ResponseUserDto;
import innowise.bankingApp.model.entity.PaymentCardEntity;
import innowise.bankingApp.model.entity.UserEntity;

import java.time.LocalDate;

public final class ServiceTestData {
    public static final LocalDate BIRTH_DATE = LocalDate.of(2000, 1, 15);
    public static final LocalDate EXPIRATION_DATE = LocalDate.now().plusYears(2);
    public static final String CARD_NUMBER = "4111111111111111";
    public static final String MASKED_NUMBER = "************1111";

    private ServiceTestData() {
    }

    public static CreateUserDto userRequest() {
        return new CreateUserDto("Jan", "Kowalski", BIRTH_DATE, "jan@example.com");
    }

    public static CreatePaymentCardDto cardRequest(Long userId) {
        return new CreatePaymentCardDto(userId, CARD_NUMBER, "Jan Kowalski", EXPIRATION_DATE);
    }

    public static UserEntity user(Long id) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setName("Jan");
        user.setSurname("Kowalski");
        user.setBirthDate(BIRTH_DATE);
        user.setEmail("jan@example.com");
        user.setActive(true);
        return user;
    }

    public static PaymentCardEntity card(Long id, UserEntity owner) {
        PaymentCardEntity card = new PaymentCardEntity();
        card.setId(id);
        card.setNumber(CARD_NUMBER);
        card.setHolder("Jan Kowalski");
        card.setExpirationDate(EXPIRATION_DATE);
        card.setActive(true);
        owner.addCard(card);
        return card;
    }

    public static ResponseUserDto userResponse(UserEntity user) {
        return userResponse(user, user.isActive());
    }

    public static ResponseUserDto userResponse(UserEntity user, boolean active) {
        return new ResponseUserDto(user.getId(), user.getName(), user.getSurname(),
                user.getBirthDate(), user.getEmail(), active);
    }

    public static ResponsePaymentCardDto cardResponse(PaymentCardEntity card) {
        return cardResponse(card, card.isActive());
    }

    public static ResponsePaymentCardDto cardResponse(PaymentCardEntity card, boolean active) {
        return new ResponsePaymentCardDto(card.getId(), card.getUser().getId(), MASKED_NUMBER,
                card.getHolder(), card.getExpirationDate(), active);
    }
}

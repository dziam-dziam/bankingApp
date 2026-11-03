package innowise.bankingApp.services.interfaces;

import innowise.bankingApp.model.dto.request.CreatePaymentCardDto;
import innowise.bankingApp.model.dto.request.UpdatePaymentCardDto;
import innowise.bankingApp.model.dto.response.ResponsePaymentCardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CardService {

    ResponsePaymentCardDto createNewPaymentCard(CreatePaymentCardDto createDto);

    ResponsePaymentCardDto getPaymentCard(Long paymentCardId);

    Page<ResponsePaymentCardDto> getAllPaymentCards(String holderName, String holderSurname, Pageable pageable);

    List<ResponsePaymentCardDto> getAllPaymentCardsByUserId(Long userId);

    ResponsePaymentCardDto updatePaymentCard(Long cardId, UpdatePaymentCardDto updateDto);

    void deletePaymentCard(Long cardId);

    void deleteAllPaymentCards();

    ResponsePaymentCardDto activatePaymentCard(Long cardId);

    ResponsePaymentCardDto deactivatePaymentCard(Long cardId);
}

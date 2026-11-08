package innowise.bankingApp.service;

import innowise.bankingApp.model.dto.response.ResponseOrderDetailsDto;
import innowise.bankingApp.model.dto.response.ResponseOrderDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    Page<ResponseOrderDto> getAllOrders(
            @NotNull Pageable pageable
    );

    ResponseOrderDetailsDto getOrder(
            @NotNull @Positive Long id
    );
}
package innowise.bankingApp.service;

import innowise.bankingApp.model.dto.request.CreateOrderDto;
import innowise.bankingApp.model.dto.request.UpdateOrderDto;
import innowise.bankingApp.model.dto.response.ResponseOrderDetailsDto;
import innowise.bankingApp.model.dto.response.ResponseOrderDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface OrderService {

    ResponseOrderDetailsDto createOrder(@NotNull @Valid CreateOrderDto dto);

    ResponseOrderDetailsDto getOrder(@NotNull @Positive Long id);

    Page<ResponseOrderDto> getAllOrders(@NotNull Pageable pageable);

    Page<ResponseOrderDto> getAllOrders(
            Instant createdFrom,
            Instant createdTo,
            Collection<@NotBlank String> statuses,
            @NotNull Pageable pageable
    );

    List<ResponseOrderDto> getOrdersByUserId(@NotNull @Positive Long userId);

    ResponseOrderDetailsDto updateOrder(
            @NotNull @Positive Long id, @NotNull @Valid UpdateOrderDto dto
    );

    void deleteOrder(@NotNull @Positive Long id);
}
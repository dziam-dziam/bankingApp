package innowise.bankingApp.service;

import innowise.bankingApp.model.dto.request.CreateItemDto;
import innowise.bankingApp.model.dto.request.UpdateItemDto;
import innowise.bankingApp.model.dto.response.ResponseItemDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemService {

    ResponseItemDto createItem(
            @NotNull @Valid CreateItemDto dto
    );

    ResponseItemDto getItem(
            @NotNull @Positive Long id
    );

    Page<ResponseItemDto> getAllItems(
            @NotNull Pageable pageable
    );

    ResponseItemDto updateItem(
            @NotNull @Positive Long id,
            @NotNull @Valid UpdateItemDto dto
    );

    void deleteItem(
            @NotNull @Positive Long id
    );
}
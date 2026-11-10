package innowise.bankingApp.mapper;

import innowise.bankingApp.model.dto.request.CreateItemDto;
import innowise.bankingApp.model.dto.request.UpdateItemDto;
import innowise.bankingApp.model.dto.response.ResponseItemDto;
import innowise.bankingApp.model.entity.ItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    ItemEntity toEntity(CreateItemDto dto);

    ResponseItemDto toDto(ItemEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    ItemEntity updateEntity(
            UpdateItemDto dto,
            @MappingTarget ItemEntity entity
    );
}
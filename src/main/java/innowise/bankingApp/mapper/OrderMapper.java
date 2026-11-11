package innowise.bankingApp.mapper;

import innowise.bankingApp.model.dto.response.ResponseOrderDetailsDto;
import innowise.bankingApp.model.dto.response.ResponseOrderDto;
import innowise.bankingApp.model.dto.response.ResponseOrderItemDto;
import innowise.bankingApp.model.entity.OrderEntity;
import innowise.bankingApp.model.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import innowise.bankingApp.model.dto.request.CreateOrderDto;
import innowise.bankingApp.model.dto.request.UpdateOrderDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.id")
    ResponseOrderDto toDto(OrderEntity entity);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "items", source = "orderItems")
    ResponseOrderDetailsDto toDetailsDto(OrderEntity entity);

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "itemName", source = "item.name")
    ResponseOrderItemDto toDto(OrderItemEntity entity);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "status", source = "status")
    OrderEntity toEntity(CreateOrderDto dto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "status", source = "status")
    OrderEntity updateEntity(UpdateOrderDto dto, @MappingTarget OrderEntity order);

    default List<ResponseOrderItemDto> mapOrderItems(
            List<OrderItemEntity> entities
    ) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .sorted(Comparator.comparing(OrderItemEntity::getId))
                .map(this::toDto)
                .toList();
    }
}
package innowise.bankingApp.mappers;

import innowise.bankingApp.model.dtos.CreateDtos.CreatePaymentCardDto;
import innowise.bankingApp.model.dtos.ResponseDtos.ResponsePaymentCardDto;
import innowise.bankingApp.model.entities.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PaymentCardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "active", ignore = true)
    PaymentCard toEntity(CreatePaymentCardDto dto);

    @Mapping(target = "userId", source = "user.id")
    ResponsePaymentCardDto toDto(PaymentCard entity);
}

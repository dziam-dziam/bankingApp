package innowise.bankingApp.Mappers;

import innowise.bankingApp.Dtos.CreateDtos.CreatePaymentCardDto;
import innowise.bankingApp.Dtos.ResponseDtos.ResponsePaymentCardDto;
import innowise.bankingApp.Entities.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PaymentCardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "active", ignore = true)
    PaymentCard toEntity(CreatePaymentCardDto dto);

    ResponsePaymentCardDto toDto(PaymentCard entity);
}

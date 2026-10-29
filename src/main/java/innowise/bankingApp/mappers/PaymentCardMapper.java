package innowise.bankingApp.mappers;

import innowise.bankingApp.model.dto.request.CreatePaymentCardDto;
import innowise.bankingApp.model.dto.response.ResponsePaymentCardDto;
import innowise.bankingApp.model.dto.request.UpdatePaymentCardDto;
import innowise.bankingApp.model.entity.PaymentCard;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PaymentCardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "active", ignore = true)
    PaymentCard toEntity(CreatePaymentCardDto dto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "number", source = "number", qualifiedByName = "maskCardNumber")
    ResponsePaymentCardDto toDto(PaymentCard entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "active", ignore = true)
    PaymentCard updateEntity(UpdatePaymentCardDto dto, @MappingTarget PaymentCard paymentCard);

    @Named("maskCardNumber")
    default String maskCardNumber(String number) {
        if (number == null) {
            return null;
        }

        if (number.length() <= 4) {
            return "*".repeat(number.length());
        }

        int visibleFrom = number.length() - 4;

        return "*".repeat(visibleFrom) + number.substring(visibleFrom);
    }
}

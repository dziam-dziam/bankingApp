package innowise.bankingApp.mappers;

import innowise.bankingApp.model.dto.create.CreatePaymentCardDto;
import innowise.bankingApp.model.dto.response.ResponsePaymentCardDto;
import innowise.bankingApp.model.dto.update.UpdatePaymentCardDto;
import innowise.bankingApp.model.entity.PaymentCard;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PaymentCardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "active", ignore = true)
    PaymentCard toEntity(CreatePaymentCardDto dto);

    @Mapping(target = "userId", source = "user.id")
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

package innowise.bankingApp.mappers;

import innowise.bankingApp.model.dtos.CreateDtos.CreateUserDto;
import innowise.bankingApp.model.dtos.ResponseDtos.ResponseUserDto;
import innowise.bankingApp.model.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentCards", ignore = true)
    @Mapping(target = "active", ignore = true)
    User toEntity(CreateUserDto dto);

    ResponseUserDto toDto(User entity);


}

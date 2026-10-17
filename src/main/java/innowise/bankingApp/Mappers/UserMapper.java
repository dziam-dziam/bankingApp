package innowise.bankingApp.Mappers;

import innowise.bankingApp.Dtos.CreateDtos.CreateUserDto;
import innowise.bankingApp.Dtos.ResponseDtos.ResponseUserDto;
import innowise.bankingApp.Entities.User;
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

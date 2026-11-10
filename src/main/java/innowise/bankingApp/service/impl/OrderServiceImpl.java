package innowise.bankingApp.service.impl;

import innowise.bankingApp.exception.OrderNotFoundByIdException;
import innowise.bankingApp.mapper.OrderMapper;
import innowise.bankingApp.model.dto.response.ResponseOrderDetailsDto;
import innowise.bankingApp.model.dto.response.ResponseOrderDto;
import innowise.bankingApp.repository.OrderRepository;
import innowise.bankingApp.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Transactional(value = "transactionManager", readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public Page<ResponseOrderDto> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toDto);
    }

    @Override
    public ResponseOrderDetailsDto getOrder(Long id) {
        return orderMapper.toDetailsDto(
                orderRepository.findDetailsById(id)
                        .orElseThrow(() ->
                                new OrderNotFoundByIdException(id)
                        )
        );
    }
}
package innowise.bankingApp.service.impl;

import innowise.bankingApp.exception.ItemNotFoundByIdException;
import innowise.bankingApp.exception.OrderNotFoundByIdException;
import innowise.bankingApp.exception.UserNotFoundByIdException;
import innowise.bankingApp.mapper.OrderMapper;
import innowise.bankingApp.model.dto.request.CreateOrderDto;
import innowise.bankingApp.model.dto.request.OrderLineDto;
import innowise.bankingApp.model.dto.request.UpdateOrderDto;
import innowise.bankingApp.model.dto.response.ResponseOrderDetailsDto;
import innowise.bankingApp.model.dto.response.ResponseOrderDto;
import innowise.bankingApp.model.entity.ItemEntity;
import innowise.bankingApp.model.entity.OrderEntity;
import innowise.bankingApp.model.entity.OrderItemEntity;
import innowise.bankingApp.model.entity.UserEntity;
import innowise.bankingApp.repository.ItemRepository;
import innowise.bankingApp.repository.OrderItemRepository;
import innowise.bankingApp.repository.OrderRepository;
import innowise.bankingApp.repository.UserRepository;
import innowise.bankingApp.service.OrderService;
import innowise.bankingApp.specification.OrderSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Validated
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Transactional(value = "transactionManager", readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional("transactionManager")
    public ResponseOrderDetailsDto createOrder(CreateOrderDto dto) {
        UserEntity user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundByIdException(dto.userId()));

        OrderEntity order = orderMapper.toEntity(dto);
        order.setUser(user);
        addOrderItems(order, dto.items());

        OrderEntity savedOrder = orderRepository.saveAndFlush(order);
        return orderMapper.toDetailsDto(savedOrder);
    }

    @Override
    public ResponseOrderDetailsDto getOrder(Long id) {
        OrderEntity order = orderRepository.findDetailsById(id)
                .orElseThrow(() -> new OrderNotFoundByIdException(id));

        return orderMapper.toDetailsDto(order);
    }

    @Override
    public Page<ResponseOrderDto> getAllOrders(Pageable pageable) {
        return getAllOrders(null, null, null, pageable);
    }

    @Override
    public Page<ResponseOrderDto> getAllOrders(
            Instant createdFrom, Instant createdTo, Collection<String> statuses, Pageable pageable
    ) {
        return orderRepository.findAll(OrderSpecifications.filter(createdFrom, createdTo, statuses), pageable)
                .map(orderMapper::toDto);
    }

    @Override
    public List<ResponseOrderDto> getOrdersByUserId(Long userId) {
        return orderRepository.findAllByUser_IdOrderByIdAsc(userId).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    @Transactional("transactionManager")
    public ResponseOrderDetailsDto updateOrder(Long id, UpdateOrderDto dto) {
        OrderEntity order = findForUpdate(id);
        orderMapper.updateEntity(dto, order);

        if (dto.items() != null) {
            orderItemRepository.deleteAll(order.getOrderItems());
            order.getOrderItems().clear();
            addOrderItems(order, dto.items());
        }

        orderRepository.flush();
        return orderMapper.toDetailsDto(order);
    }

    @Override
    @Transactional("transactionManager")
    public void deleteOrder(Long id) {
        OrderEntity order = findForUpdate(id);
        order.setDeleted(true);
    }

    private OrderEntity findForUpdate(Long id) {
        return orderRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new OrderNotFoundByIdException(id));
    }

    private void addOrderItems(OrderEntity order, List<OrderLineDto> lines) {
        List<Long> itemIds = lines.stream().map(OrderLineDto::itemId).distinct().toList();
        Map<Long, ItemEntity> itemsById = new HashMap<>();

        for (ItemEntity item : itemRepository.findAllById(itemIds)) {
            itemsById.put(item.getId(), item);
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderLineDto line : lines) {
            ItemEntity item = itemsById.get(line.itemId());
            if (item == null) {
                throw new ItemNotFoundByIdException(line.itemId());
            }

            OrderItemEntity position = new OrderItemEntity();
            position.setItem(item);
            position.setQuantity(line.quantity());
            order.addOrderItem(position);

            BigDecimal linePrice = item.getPrice().multiply(BigDecimal.valueOf(line.quantity()));
            totalPrice = totalPrice.add(linePrice);
        }

        order.setTotalPrice(totalPrice);
    }
}
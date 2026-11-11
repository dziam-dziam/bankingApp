package innowise.bankingApp.model.entity;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class OrderPersistenceTest {

    private static final Instant CREATED_TIME =
            Instant.parse("2026-11-08T12:00:00Z");

    private static final Instant UPDATED_TIME =
            CREATED_TIME.plusSeconds(60);

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private AuditingHandler auditingHandler;

    private Instant auditTime;

    @BeforeEach
    void configureAuditTime() {
        auditTime = CREATED_TIME;
        auditingHandler.setDateTimeProvider(() -> Optional.of(auditTime));
    }

    @AfterEach
    void restoreAuditTimeProvider() {
        auditingHandler.setDateTimeProvider(null);
    }

    @Test
    void shouldReloadBothSidesOfRelations() {
        OrderItemEntity first = persistPosition();

        OrderItemEntity second = new OrderItemEntity();
        second.setQuantity(3);
        first.getOrder().addOrderItem(second);
        first.getItem().addOrderItem(second);
        entityManager.persist(second);
        entityManager.flush();

        Long orderId = first.getOrder().getId();
        Long itemId = first.getItem().getId();
        Long userId = first.getOrder().getUser().getId();
        Long positionId = first.getId();

        assertNotNull(positionId);
        assertNotNull(second.getId());
        entityManager.clear();

        OrderEntity order = entityManager.find(OrderEntity.class, orderId);
        ItemEntity item = entityManager.find(ItemEntity.class, itemId);
        OrderItemEntity position =
                entityManager.find(OrderItemEntity.class, positionId);

        assertEquals(2, order.getOrderItems().size());
        assertEquals(2, item.getOrderItems().size());
        assertTrue(order.getOrderItems().contains(position));
        assertTrue(item.getOrderItems().contains(position));

        assertEquals(orderId, position.getOrder().getId());
        assertEquals(itemId, position.getItem().getId());
        assertEquals(userId, order.getUser().getId());

        assertEquals(2, position.getQuantity());
        assertEquals(new BigDecimal("25.00"), order.getTotalPrice());
        assertEquals(new BigDecimal("12.50"), item.getPrice());
        assertFalse(order.isDeleted());
    }

    @Test
    void shouldPersistChangedOrderAndItemReferences() {
        OrderItemEntity position = persistPosition();
        Long previousOrderId = position.getOrder().getId();
        Long previousItemId = position.getItem().getId();

        OrderEntity nextOrder = newOrder(position.getOrder().getUser());
        ItemEntity nextItem = newItem();
        entityManager.persist(nextOrder);
        entityManager.persist(nextItem);

        nextOrder.addOrderItem(position);
        nextItem.addOrderItem(position);
        entityManager.flush();

        Long positionId = position.getId();
        Long nextOrderId = nextOrder.getId();
        Long nextItemId = nextItem.getId();
        entityManager.clear();

        OrderItemEntity reloaded =
                entityManager.find(OrderItemEntity.class, positionId);

        OrderEntity previousOrder =
                entityManager.find(OrderEntity.class, previousOrderId);

        ItemEntity previousItem =
                entityManager.find(ItemEntity.class, previousItemId);

        assertEquals(nextOrderId, reloaded.getOrder().getId());
        assertEquals(nextItemId, reloaded.getItem().getId());

        assertTrue(previousOrder.getOrderItems().isEmpty());
        assertTrue(previousItem.getOrderItems().isEmpty());
        assertEquals(1, reloaded.getOrder().getOrderItems().size());
        assertEquals(1, reloaded.getItem().getOrderItems().size());
    }

    @Test
    void shouldAuditCreationAndUpdateOfAllThreeEntities() {
        OrderItemEntity position = persistPosition();
        OrderEntity order = position.getOrder();
        ItemEntity item = position.getItem();

        Long orderId = order.getId();
        Long itemId = item.getId();
        Long positionId = position.getId();

        assertAuditDates(List.of(order, item, position), CREATED_TIME);

        auditTime = UPDATED_TIME;
        order.setStatus("PROCESSING");
        item.setName("Updated item");
        position.setQuantity(3);

        entityManager.flush();
        entityManager.clear();

        OrderEntity updatedOrder =
                entityManager.find(OrderEntity.class, orderId);

        ItemEntity updatedItem =
                entityManager.find(ItemEntity.class, itemId);

        OrderItemEntity updatedPosition =
                entityManager.find(OrderItemEntity.class, positionId);

        assertEquals("PROCESSING", updatedOrder.getStatus());
        assertEquals("Updated item", updatedItem.getName());
        assertEquals(3, updatedPosition.getQuantity());

        assertAuditDates(
                List.of(updatedOrder, updatedItem, updatedPosition),
                UPDATED_TIME
        );
    }

    private OrderItemEntity persistPosition() {
        UserEntity user = new UserEntity();
        user.setName("Jan");
        user.setSurname("Kowalski");
        user.setBirthDate(LocalDate.of(2000, 1, 15));
        user.setEmail("orders-" + UUID.randomUUID() + "@example.com");
        user.setActive(true);
        entityManager.persist(user);

        OrderEntity order = newOrder(user);
        ItemEntity item = newItem();
        entityManager.persist(order);
        entityManager.persist(item);

        OrderItemEntity position = new OrderItemEntity();
        position.setQuantity(2);

        order.addOrderItem(position);
        item.addOrderItem(position);

        entityManager.persist(position);
        entityManager.flush();

        return position;
    }

    private OrderEntity newOrder(UserEntity user) {
        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setStatus("NEW");
        order.setTotalPrice(new BigDecimal("25.00"));
        return order;
    }

    private ItemEntity newItem() {
        ItemEntity item = new ItemEntity();
        item.setName("Sample item");
        item.setPrice(new BigDecimal("12.50"));
        return item;
    }

    private void assertAuditDates(
            List<AuditableEntity> entities,
            Instant expectedUpdatedAt
    ) {
        for (AuditableEntity entity : entities) {
            assertEquals(CREATED_TIME, entity.getCreatedAt());
            assertEquals(expectedUpdatedAt, entity.getUpdatedAt());
        }
    }
}
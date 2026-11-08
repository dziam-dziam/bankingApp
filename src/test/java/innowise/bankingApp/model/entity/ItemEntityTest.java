package innowise.bankingApp.model.entity;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemEntityTest {

    @Test
    void shouldAddPositionToItemWithoutDuplicates() {
        ItemEntity item = new ItemEntity();
        OrderItemEntity position = new OrderItemEntity();

        item.addOrderItem(position);
        item.addOrderItem(position);

        assertSame(item, position.getItem());
        assertEquals(1, item.getOrderItems().size());
        assertSame(position, item.getOrderItems().get(0));
    }

    @Test
    void shouldMovePositionToAnotherItemAndKeepOtherPositions() {
        ItemEntity previousItem = new ItemEntity();
        ItemEntity nextItem = new ItemEntity();
        OrderItemEntity moved = new OrderItemEntity();
        OrderItemEntity retained = new OrderItemEntity();

        previousItem.addOrderItem(moved);
        previousItem.addOrderItem(retained);

        nextItem.addOrderItem(moved);

        assertSame(nextItem, moved.getItem());
        assertSame(previousItem, retained.getItem());

        assertEquals(1, previousItem.getOrderItems().size());
        assertSame(retained, previousItem.getOrderItems().get(0));

        assertEquals(1, nextItem.getOrderItems().size());
        assertSame(moved, nextItem.getOrderItems().get(0));
    }

    @Test
    void shouldRespectItemEqualityContract() {
        assertEqualityContract(ItemEntity::new, ItemEntity::setId);
    }

    private static <T> void assertEqualityContract(
            Supplier<T> factory,
            BiConsumer<T, Long> setId
    ) {
        T first = factory.get();
        T second = factory.get();
        T third = factory.get();
        T alias = first;

        assertEquals(first, alias);
        assertNotEquals(first, second);
        assertFalse(first.equals(null));
        assertNotEquals(first, new Object());

        Set<T> entities = new HashSet<>();
        entities.add(first);
        setId.accept(first, 1L);

        assertTrue(entities.contains(first));
        assertNotEquals(first, second);
        assertNotEquals(second, first);

        setId.accept(second, 1L);
        setId.accept(third, 1L);

        assertEquals(first, second);
        assertEquals(second, first);
        assertEquals(second, third);
        assertEquals(first, third);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(entities.contains(second));

        setId.accept(second, 2L);

        assertNotEquals(first, second);
        assertNotEquals(second, first);
    }
}
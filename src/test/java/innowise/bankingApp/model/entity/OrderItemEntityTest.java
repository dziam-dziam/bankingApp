package innowise.bankingApp.model.entity;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderItemEntityTest {

    @Test
    void shouldRespectOrderItemEqualityContract() {
        assertEqualityContract(
                OrderItemEntity::new,
                OrderItemEntity::setId
        );
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
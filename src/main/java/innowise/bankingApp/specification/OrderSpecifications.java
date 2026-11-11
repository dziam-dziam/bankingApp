package innowise.bankingApp.specification;

import innowise.bankingApp.model.entity.OrderEntity;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class OrderSpecifications {

    private OrderSpecifications() {
    }

    public static Specification<OrderEntity> filter(
            Instant createdFrom, Instant createdTo, Collection<String> statuses
    ) {
        if (createdFrom != null && createdTo != null && createdFrom.isAfter(createdTo)) {
            throw new IllegalArgumentException("createdFrom must be before or equal to createdTo");
        }

        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Path<Instant> createdAt = root.get("createdAt");

            if (createdFrom != null) {
                predicates.add(builder.greaterThanOrEqualTo(createdAt, createdFrom));
            }

            if (createdTo != null) {
                predicates.add(builder.lessThanOrEqualTo(createdAt, createdTo));
            }

            if (statuses != null && !statuses.isEmpty()) {
                predicates.add(root.get("status").in(statuses));
            }

            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
package innowise.bankingApp.specification;

import innowise.bankingApp.model.entity.PaymentCardEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PaymentCardSpecifications {

    public static Specification<PaymentCardEntity> filter(
            String name,
            String surname
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("user").get("name")),
                                "%" + name.trim()
                                        .toLowerCase(Locale.ROOT) + "%"
                        )
                );
            }

            if (surname != null && !surname.isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("user").get("surname")),
                                "%" + surname.trim()
                                        .toLowerCase(Locale.ROOT) + "%"
                        )
                );
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
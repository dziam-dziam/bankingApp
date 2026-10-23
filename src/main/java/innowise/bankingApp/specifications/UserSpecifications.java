package innowise.bankingApp.specifications;

import innowise.bankingApp.model.entities.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class UserSpecifications {

    public static Specification<User> filter(String name, String surname) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();

            if (name != null && !name.isBlank()) {
                predicateList.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("name")), "%" + name.trim().toLowerCase(Locale.ROOT) + "%"
                        )
                );
            }

            if (surname != null && !surname.isBlank()) {
                predicateList.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("surname")), "%" + surname.trim().toLowerCase(Locale.ROOT) + "%"
                        )
                );
            }

            return criteriaBuilder.and(predicateList.toArray(Predicate[]::new));
        }
        );
    }
}

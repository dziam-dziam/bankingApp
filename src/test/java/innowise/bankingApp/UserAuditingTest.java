package innowise.bankingApp;

import innowise.bankingApp.model.entities.PaymentCard;
import innowise.bankingApp.model.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class UserAuditingTest {

    private static final Instant CREATED_TIME = Instant.parse("2026-10-11T10:00:00Z");
    private static final Instant UPDATED_TIME = CREATED_TIME.plusSeconds(60);

    @PersistenceContext
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
    void shouldSetAuditDatesOnUserInsert() {
        User user = newUser();

        entityManager.persist(user);
        entityManager.flush();
        Long id = user.getId();
        assertNotNull(id);
        entityManager.clear();

        User saved = entityManager.find(User.class, id);

        assertNotNull(saved);
        assertEquals("Jan", saved.getName());
        assertEquals(CREATED_TIME, saved.getCreatedAt());
        assertEquals(CREATED_TIME, saved.getUpdatedAt());
    }

    @Test
    void shouldPreserveCreatedAtAndChangeUpdatedAtOnUserUpdate() {
        User user = newUser();
        entityManager.persist(user);
        entityManager.flush();
        Long id = user.getId();
        entityManager.clear();

        auditTime = UPDATED_TIME;
        User managed = entityManager.find(User.class, id);
        managed.setName("Anna");
        entityManager.flush();
        entityManager.clear();

        User updated = entityManager.find(User.class, id);

        assertNotNull(updated);
        assertEquals("Anna", updated.getName());
        assertEquals(CREATED_TIME, updated.getCreatedAt());
        assertEquals(UPDATED_TIME, updated.getUpdatedAt());
        assertTrue(updated.getUpdatedAt().isAfter(updated.getCreatedAt()));
    }

    @Test
    void shouldSaveTwoAuditedCardsForSameUser() {
        User user = newUser();
        entityManager.persist(user);

        PaymentCard firstCard = newCard(user, "4111111111111111");
        PaymentCard secondCard = newCard(user, "5555555555554444");
        entityManager.persist(firstCard);
        entityManager.persist(secondCard);
        entityManager.flush();
        Long userId = user.getId();
        entityManager.clear();

        List<PaymentCard> cards = entityManager.createQuery(
                        "select c from PaymentCard c where c.user.id = :userId order by c.id",
                        PaymentCard.class)
                .setParameter("userId", userId)
                .getResultList();

        assertEquals(2, cards.size());
        assertNotEquals(cards.get(0).getId(), cards.get(1).getId());
        assertEquals("4111111111111111", cards.get(0).getNumber());
        assertEquals("5555555555554444", cards.get(1).getNumber());
        for (PaymentCard card : cards) {
            assertEquals(userId, card.getUser().getId());
            assertEquals(CREATED_TIME, card.getCreatedAt());
            assertEquals(CREATED_TIME, card.getUpdatedAt());
        }
    }

    @Test
    void shouldPreserveCreatedAtAndChangeUpdatedAtOnCardUpdate() {
        User user = newUser();
        entityManager.persist(user);
        PaymentCard card = newCard(user, "4111111111111111");
        entityManager.persist(card);
        entityManager.flush();
        Long id = card.getId();
        entityManager.clear();

        auditTime = UPDATED_TIME;
        PaymentCard managed = entityManager.find(PaymentCard.class, id);
        managed.setActive(false);
        entityManager.flush();
        entityManager.clear();

        PaymentCard updated = entityManager.find(PaymentCard.class, id);

        assertNotNull(updated);
        assertFalse(updated.isActive());
        assertEquals(CREATED_TIME, updated.getCreatedAt());
        assertEquals(UPDATED_TIME, updated.getUpdatedAt());
        assertTrue(updated.getUpdatedAt().isAfter(updated.getCreatedAt()));
    }

    private User newUser() {
        User user = new User();
        user.setName("Jan");
        user.setSurname("Kowalski");
        user.setBirthDate(LocalDate.of(2000, 1, 15));
        user.setEmail("jan.kowalski@example.com");
        user.setActive(true);
        return user;
    }

    private PaymentCard newCard(User user, String number) {
        PaymentCard card = new PaymentCard();
        card.setUser(user);
        card.setNumber(number);
        card.setHolder("Jan Kowalski");
        card.setExpirationDate(LocalDate.of(2030, 12, 31));
        card.setActive(true);
        return card;
    }
}

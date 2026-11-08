package innowise.bankingApp.integration;

import innowise.bankingApp.model.entity.PaymentCardEntity;
import innowise.bankingApp.model.entity.UserEntity;
import innowise.bankingApp.repository.PaymentCardRepository;
import innowise.bankingApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.cache.type=none",
        "spring.docker.compose.enabled=false",
        "spring.jpa.open-in-view=false"
})
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("integration")
@WithMockUser(username = "test-user")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class CardFlowTest {

    @Container
    private static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:18-alpine")
                    .withDatabaseName("card_flow_test")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name",
                () -> "org.postgresql.Driver");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    private UserEntity user;
    private final LocalDate expirationDate = LocalDate.now().plusYears(2);

    @BeforeEach
    void setUp() {
        // Usun dane poprzedniego testu: najpierw karty, potem uzytkownikow.
        paymentCardRepository.deleteAll();
        userRepository.deleteAll();

        // Kazdy test dostaje wlasnego uzytkownika, bez kart.
        user = saveUser("maciek@example.com");
    }

    @Test
    void shouldCreatePaymentCard() throws Exception {
        // given
        String createPaymentCardJson = """
                {
                  "userId": %d,
                  "number": "1111222233334444",
                  "holder": "Maciek Kowalski",
                  "expirationDate": "%s"
                }
                """.formatted(user.getId(), expirationDate);

        // when then - odpowiedz kontrolera
        mockMvc.perform(post("/paymentCard/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPaymentCardJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.number").value("************4444"))
                .andExpect(jsonPath("$.holder").value("Maciek Kowalski"))
                .andExpect(jsonPath("$.expirationDate").value(expirationDate.toString()))
                .andExpect(jsonPath("$.active").value(true));

        // then - zapis w bazie
        var cards = paymentCardRepository.findAllByUserId(user.getId());
        assertEquals(1, cards.size());

        PaymentCardEntity savedCard = cards.getFirst();
        assertEquals("1111222233334444", savedCard.getNumber());
        assertEquals("Maciek Kowalski", savedCard.getHolder());
        assertEquals(expirationDate, savedCard.getExpirationDate());
        assertTrue(savedCard.isActive());
    }

    @Test
    void shouldGetPaymentCardById() throws Exception {
        // given
        PaymentCardEntity card = saveCard(user, "1111222233334444", true);

        // when then
        mockMvc.perform(get("/paymentCard/get/{id}", card.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(card.getId()))
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.number").value("************4444"))
                .andExpect(jsonPath("$.holder").value("Maciek Kowalski"))
                .andExpect(jsonPath("$.expirationDate").value(expirationDate.toString()))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldGetAllPaymentCards() throws Exception {
        // given
        PaymentCardEntity cardOne = saveCard(user, "1111222233334444", true);
        PaymentCardEntity cardTwo = saveCard(user, "5555666677778888", true);

        // when then - Page przechowuje karty w polu content
        mockMvc.perform(get("/paymentCard/getAll")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(cardOne.getId()))
                .andExpect(jsonPath("$.content[0].number").value("************4444"))
                .andExpect(jsonPath("$.content[1].id").value(cardTwo.getId()))
                .andExpect(jsonPath("$.content[1].number").value("************8888"));
    }

    @Test
    void shouldGetAllPaymentCardsByUserId() throws Exception {
        // given - dwie karty szukanego uzytkownika
        saveCard(user, "1111222233334444", true);
        saveCard(user, "5555666677778888", true);

        // Karta drugiego uzytkownika nie powinna znalezc sie w odpowiedzi.
        UserEntity otherUser = saveUser("other@example.com");
        saveCard(otherUser, "9999000011112222", true);

        // when then - List jest bezposrednio tablica JSON
        mockMvc.perform(get("/paymentCard/getAllById/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value(user.getId()))
                .andExpect(jsonPath("$[1].userId").value(user.getId()))
                .andExpect(jsonPath("$[*].number", containsInAnyOrder(
                        "************4444", "************8888")));
    }

    @Test
    void shouldDeletePaymentCard() throws Exception {
        // given
        PaymentCardEntity cardOne = saveCard(user, "1111222233334444", true);
        PaymentCardEntity cardTwo = saveCard(user, "5555666677778888", true);

        // when then
        mockMvc.perform(delete("/paymentCard/delete/{cardId}", cardOne.getId())
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        // then - usunieto tylko wskazana karte
        assertFalse(paymentCardRepository.existsById(cardOne.getId()));
        assertTrue(paymentCardRepository.existsById(cardTwo.getId()));
        assertEquals(1L, paymentCardRepository.count());
        assertTrue(userRepository.existsById(user.getId()));
    }

    @Test
    void shouldUpdatePaymentCard() throws Exception {
        // given
        PaymentCardEntity card = saveCard(user, "1111222233334444", true);
        LocalDate newExpirationDate = expirationDate.plusYears(1);

        String updatePaymentCardJson = """
                {
                  "id": %d,
                  "number": "5555666677778888",
                  "holder": "Adam Nowak",
                  "expirationDate": "%s"
                }
                """.formatted(card.getId(), newExpirationDate);

        // when then - odpowiedz kontrolera
        mockMvc.perform(put("/paymentCard/update/{cardId}", card.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePaymentCardJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(card.getId()))
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.number").value("************8888"))
                .andExpect(jsonPath("$.holder").value("Adam Nowak"))
                .andExpect(jsonPath("$.expirationDate").value(newExpirationDate.toString()))
                .andExpect(jsonPath("$.active").value(true));

        // then - zaktualizowany rekord w bazie
        PaymentCardEntity updatedCard = paymentCardRepository.findById(card.getId())
                .orElseThrow();

        assertEquals("5555666677778888", updatedCard.getNumber());
        assertEquals("Adam Nowak", updatedCard.getHolder());
        assertEquals(newExpirationDate, updatedCard.getExpirationDate());
        assertTrue(updatedCard.isActive());
        assertEquals(1L, paymentCardRepository.count());
    }

    @Test
    void shouldDeleteAllPaymentCards() throws Exception {
        // given
        saveCard(user, "1111222233334444", true);
        saveCard(user, "5555666677778888", true);

        // when then
        mockMvc.perform(delete("/paymentCard/deleteAll")
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        // then - karty usuniete, uzytkownik nadal istnieje
        assertEquals(0L, paymentCardRepository.count());
        assertTrue(userRepository.existsById(user.getId()));
    }

    @Test
    void shouldActivatePaymentCard() throws Exception {
        // given - karta jest nieaktywna
        PaymentCardEntity card = saveCard(user, "1111222233334444", false);

        // when then
        mockMvc.perform(put("/paymentCard/activate/{cardId}", card.getId())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(card.getId()))
                .andExpect(jsonPath("$.active").value(true));

        // then - stan w bazie
        PaymentCardEntity activatedCard = paymentCardRepository.findById(card.getId())
                .orElseThrow();

        assertTrue(activatedCard.isActive());
    }

    @Test
    void shouldDeactivatePaymentCard() throws Exception {
        // given - karta jest aktywna
        PaymentCardEntity card = saveCard(user, "1111222233334444", true);

        // when then
        mockMvc.perform(put("/paymentCard/deactivate/{cardId}", card.getId())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(card.getId()))
                .andExpect(jsonPath("$.active").value(false));

        // then - stan w bazie
        PaymentCardEntity deactivatedCard = paymentCardRepository.findById(card.getId())
                .orElseThrow();

        assertFalse(deactivatedCard.isActive());
    }

    // Metody pomocnicze przygotowuja dane w bazie, aby ich nie powtarzac.
    private UserEntity saveUser(String email) {
        UserEntity newUser = new UserEntity();
        newUser.setName("Maciek");
        newUser.setSurname("Kowalski");
        newUser.setBirthDate(LocalDate.of(2000, 1, 1));
        newUser.setEmail(email);
        newUser.setActive(true);

        return userRepository.saveAndFlush(newUser);
    }

    private PaymentCardEntity saveCard(UserEntity owner, String number, boolean active) {
        PaymentCardEntity card = new PaymentCardEntity();
        card.assignUser(owner);
        card.setNumber(number);
        card.setHolder("Maciek Kowalski");
        card.setExpirationDate(expirationDate);
        card.setActive(active);

        return paymentCardRepository.saveAndFlush(card);
    }
}

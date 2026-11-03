package innowise.bankingApp.integration;

import innowise.bankingApp.model.entity.PaymentCardEntity;
import innowise.bankingApp.model.entity.UserEntity;
import innowise.bankingApp.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.LocalDate;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.cache.type=redis",
        "spring.docker.compose.enabled=false",
        "spring.jpa.open-in-view=false"
})
@AutoConfigureMockMvc
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserCacheFlowTest {

    @Container
    private static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:18-alpine")
                    .withDatabaseName("user_cache_test")
                    .withUsername("test")
                    .withPassword("test");

    @Container
    private static final GenericContainer<?> REDIS =
            new GenericContainer<>("redis:8-alpine")
                    .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name",
                () -> "org.postgresql.Driver");

        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port",
                () -> REDIS.getMappedPort(6379));
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @WithMockUser(username = "test-user")
    void shouldCacheUserWithCards() throws Exception {
        // given
        UserEntity user = new UserEntity();
        user.setName("Maciek");
        user.setSurname("Kowalski");
        user.setBirthDate(LocalDate.of(2000, 1, 1));
        user.setEmail("maciek.cache@example.com");
        user.setActive(true);

        PaymentCardEntity card = new PaymentCardEntity();
        card.setNumber("1111222233334444");
        card.setHolder("Maciek Kowalski");
        card.setExpirationDate(LocalDate.now().plusYears(2));
        card.setActive(true);

        user.addCard(card);
        user = userRepository.saveAndFlush(user);

        Long userId = user.getId();

        Cache cache = cacheManager.getCache("usersWithCards");
        assertNotNull(cache);

        cache.evictIfPresent(userId);
        assertNull(cache.get(userId));

        // when
        // then
        mockMvc.perform(get("/user/getUserWithCards/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(userId))
                .andExpect(jsonPath("$.user.name").value("Maciek"))
                .andExpect(jsonPath("$.cards.length()").value(1))
                .andExpect(jsonPath("$.cards[0].userId").value(userId))
                .andExpect(jsonPath("$.cards[0].number")
                        .value("************4444"))
                .andExpect(jsonPath("$.cards[0].holder")
                        .value("Maciek Kowalski"));

        await().atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> assertNotNull(cache.get(userId)));

        // given
        int updatedRows = jdbcTemplate.update(
                "UPDATE users SET name = ? WHERE id = ?",
                "Adam", userId
        );

        assertEquals(1, updatedRows);
        assertEquals("Adam", jdbcTemplate.queryForObject(
                "SELECT name FROM users WHERE id = ?",
                String.class, userId
        ));

        // when
        mockMvc.perform(get("/user/getUserWithCards/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.name").value("Maciek"))
                .andExpect(jsonPath("$.cards.length()").value(1));

        cache.evictIfPresent(userId);
        assertNull(cache.get(userId));

        // then
        mockMvc.perform(get("/user/getUserWithCards/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.name").value("Adam"))
                .andExpect(jsonPath("$.cards.length()").value(1));

        await().atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> assertNotNull(cache.get(userId)));
    }
}
package innowise.bankingApp.integration;

import innowise.bankingApp.model.entity.UserEntity;
import innowise.bankingApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.LocalDate;
import java.util.List;

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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserFlowTest {

    @Container
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18-alpine")
            .withDatabaseName("user_flow_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        // Kazdy test zaczyna od pustej bazy w kontenerze tej klasy.
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "test-user")
    void shouldCreateNewUser() throws Exception {
        //given
        String createUserJson = """
                {
                  "name": "Damian",
                  "surname": "Majkowski",
                  "birthDate": "2002-09-27",
                  "email": "damian2002@gmail.com"
                }
                """;

        //when
        mockMvc.perform(post("/user/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUserJson))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Damian"))
                .andExpect(jsonPath("$.active").value(true));

        //then
        List<UserEntity> users = userRepository.findAll();
        assertEquals(1, users.size());

        UserEntity userEntity = users.getFirst();
        assertEquals("Damian", userEntity.getName());
        assertEquals("Majkowski", userEntity.getSurname());
        assertEquals("damian2002@gmail.com", userEntity.getEmail());
        assertTrue(userEntity.isActive());
    }

    @Test
    @WithMockUser(username = "test-user")
    void shouldGetUserById() throws Exception {
        // given
        UserEntity user = new UserEntity();
        user.setName("Maciek");
        user.setSurname("Kowalski");
        user.setBirthDate(LocalDate.of(2000, 1, 1));
        user.setEmail("maciek.get@example.com");
        user.setActive(true);

        user = userRepository.saveAndFlush(user);

        // when
        mockMvc.perform(get("/user/get/{id}", user.getId()))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value("Maciek"))
                .andExpect(jsonPath("$.surname").value("Kowalski"))
                .andExpect(jsonPath("$.birthDate").value("2000-01-01"))
                .andExpect(jsonPath("$.email").value("maciek.get@example.com"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser(username = "test-user")
    void shouldGetAllUsers() throws Exception {
        //given
        UserEntity userOne = new UserEntity();
        userOne.setName("Maciek");
        userOne.setSurname("Kowalski");
        userOne.setBirthDate(LocalDate.of(2000, 1, 1));
        userOne.setEmail("maciek.get@example.com");
        userOne.setActive(true);
        userOne = userRepository.saveAndFlush(userOne);
        UserEntity userTwo = new UserEntity();
        userTwo.setName("Maciek");
        userTwo.setSurname("Kowalski");
        userTwo.setBirthDate(LocalDate.of(2000, 1, 1));
        userTwo.setEmail("maciek.getAll@example.com");
        userTwo.setActive(true);
        userTwo = userRepository.saveAndFlush(userTwo);
        //when then
        mockMvc.perform(get("/user/getAll")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(userOne.getId()))
                .andExpect(jsonPath("$.content[0].email")
                        .value("maciek.get@example.com"))
                .andExpect(jsonPath("$.content[1].id").value(userTwo.getId()))
                .andExpect(jsonPath("$.content[1].email")
                        .value("maciek.getAll@example.com"));
    }

    @Test
    @WithMockUser(username = "test-user")
    void shouldDeleteUser () throws Exception{
        //given
        UserEntity userOne = new UserEntity();
        userOne.setName("Maciek");
        userOne.setSurname("Kowalski");
        userOne.setBirthDate(LocalDate.of(2000, 1, 1));
        userOne.setEmail("maciek.get@example.com");
        userOne.setActive(true);
        userOne = userRepository.saveAndFlush(userOne);
        UserEntity userTwo = new UserEntity();
        userTwo.setName("Maciek");
        userTwo.setSurname("Kowalski");
        userTwo.setBirthDate(LocalDate.of(2000, 1, 1));
        userTwo.setEmail("maciek.getAll@example.com");
        userTwo.setActive(true);
        userTwo = userRepository.saveAndFlush(userTwo);
        // when then
        mockMvc.perform(delete("/user/delete/{id}", userOne.getId())
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        assertFalse(userRepository.existsById(userOne.getId()));
        assertTrue(userRepository.existsById(userTwo.getId()));

    }
    @Test
    @WithMockUser(username = "test-user")
    void shouldDeleteAllUsers () throws Exception{
        //given
        UserEntity userOne = new UserEntity();
        userOne.setName("Maciek");
        userOne.setSurname("Kowalski");
        userOne.setBirthDate(LocalDate.of(2000, 1, 1));
        userOne.setEmail("maciek.get@example.com");
        userOne.setActive(true);
        userOne = userRepository.saveAndFlush(userOne);
        UserEntity userTwo = new UserEntity();
        userTwo.setName("Maciek");
        userTwo.setSurname("Kowalski");
        userTwo.setBirthDate(LocalDate.of(2000, 1, 1));
        userTwo.setEmail("maciek.getAll@example.com");
        userTwo.setActive(true);
        userTwo = userRepository.saveAndFlush(userTwo);
        // when then
        mockMvc.perform(delete("/user/deleteAll")
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        assertFalse(userRepository.existsById(userOne.getId()));
        assertFalse(userRepository.existsById(userTwo.getId()));
        assertTrue(userRepository.findAll().isEmpty());

    }

    @Test
    @WithMockUser(username = "test-user")
    void shouldUpdateUser () throws Exception {
        //given
        UserEntity userOne = new UserEntity();
        userOne.setName("Maciek");
        userOne.setSurname("Kowalski");
        userOne.setBirthDate(LocalDate.of(2000, 1, 1));
        userOne.setEmail("maciek.get@example.com");
        userOne.setActive(true);
        userOne = userRepository.saveAndFlush(userOne);

        String updateUserJson = """
                {"id" : %d,
                "name" : "Marek",
                "surname" : "Markowski",
                "birthDate" : "1995-05-10",
                "email" : "damian@gmail.com"
                }
                """.formatted(userOne.getId());

        //when
        mockMvc.perform(put("/user/update/{id}", userOne.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userOne.getId()))
                .andExpect(jsonPath("$.name").value("Marek"))
                .andExpect(jsonPath("$.surname").value("Markowski"))
                .andExpect(jsonPath("$.birthDate").value("1995-05-10"))
                .andExpect(jsonPath("$.email").value("damian@gmail.com"))
                .andExpect(jsonPath("$.active").value(true));

        //then
        UserEntity updatedUser = userRepository.findById(userOne.getId())
                .orElseThrow();
        assertEquals("Marek", updatedUser.getName());
        assertEquals("Markowski", updatedUser.getSurname());
        assertEquals(LocalDate.of(1995, 5, 10), updatedUser.getBirthDate());
        assertEquals("damian@gmail.com", updatedUser.getEmail());
        assertTrue(updatedUser.isActive());
    }

    @Test
    @WithMockUser(username = "test-user")
    void shouldActivateUser() throws Exception {
        // given
        UserEntity user = new UserEntity();
        user.setName("Maciek");
        user.setSurname("Kowalski");
        user.setBirthDate(LocalDate.of(2000, 1, 1));
        user.setEmail("maciek.activate@example.com");
        user.setActive(false);
        user = userRepository.saveAndFlush(user);

        // when then
        mockMvc.perform(put("/user/activate/{id}", user.getId())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.active").value(true));

        // then
        UserEntity activatedUser = userRepository.findById(user.getId())
                .orElseThrow();

        assertTrue(activatedUser.isActive());
    }

    @Test
    @WithMockUser(username = "test-user")
    void shouldDeactivateUser() throws Exception {
        // given
        UserEntity user = new UserEntity();
        user.setName("Maciek");
        user.setSurname("Kowalski");
        user.setBirthDate(LocalDate.of(2000, 1, 1));
        user.setEmail("maciek.deactivate@example.com");
        user.setActive(true);
        user = userRepository.saveAndFlush(user);

        // when
        mockMvc.perform(put("/user/deactivate/{id}", user.getId())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.active").value(false));

        // then
        UserEntity deactivatedUser = userRepository.findById(user.getId())
                .orElseThrow();

        assertFalse(deactivatedUser.isActive());
    }
}
